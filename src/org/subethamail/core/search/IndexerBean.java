/*
 * $Id: IndexerBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/search/IndexerBean.java $
 */

package org.subethamail.core.search;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.sql.DataSource;

import org.apache.lucene.queryParser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SearchException;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.post.OutboundMTA;
import org.subethamail.core.search.i.Indexer;
import org.subethamail.core.search.i.SimpleResult;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Mail;

/**
 * Manages the Lucene search index and provides a
 * low-level search API.  Conceptually there are two indexes, the
 * current index (which is used for searching) and the fallow index
 * (which is either nonexistant or in the process of being built).
 * This allows searches even during the rebuilding of a whole new index.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Named("indexer")
//@ApplicationScoped
@Singleton
@Startup
public class IndexerBean implements Indexer
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(IndexerBean.class);

	/** The base dir under which everything is put */
	static final File BASE_DIR = new File("/var/tmp/subetha/indexer");
	static { BASE_DIR.mkdirs(); }

	/** If this file exists, use index2 instead of index1 */
	static final File USE2 = new File(BASE_DIR, "use2");

	/** The appropriate locks for updating or rebuilding the index */
	private static ReentrantLock updateLock = new ReentrantLock();
	private static ReentrantLock rebuildLock = new ReentrantLock();
	
	/**
	 * One of two index managers will be active at a time
	 */
	static IndexMgr index1 = new IndexMgr(new File(BASE_DIR, "1"));
	static IndexMgr index2 = new IndexMgr(new File(BASE_DIR, "2"));

	@Inject Indexer ind;

	/** */
	@Named("jdbc/subetha")
	DataSource ds = null;

	/** */
	@OutboundMTA Session mailSession;

	/** */
	@SubEtha
	protected SubEthaEntityManager em;

	/**
	 * @return the currently in-use index manager.
	 */
	protected static IndexMgr getCurrentIndex()
	{
		if (USE2.exists())
			return index2;
		else
			return index1;
	}

	/**
	 * @return the index manager which is not in use.
	 */
	protected static IndexMgr getFallowIndex()
	{
		if (USE2.exists())
			return index1;
		else
			return index2;
	}

	/**
	 * Sets which index becomes the current one by
	 * modifying the existance of the USE2 file.
	 */
	protected void swapCurrentIndex() throws IOException
	{
		if (USE2.exists())
		{
			USE2.delete();
			index2.delayedDelete();
		}
		else
		{
			USE2.createNewFile();
			index1.delayedDelete();
		}
	}

	/**
	 * Makes sure that the index is properly initialized.
	 */
	public void initialize()
	{
		try
		{
			getCurrentIndex().search(0L, "asdfasfdasfdasfdasfdasfdasfd", 0, 1);
		}
		catch (ParseException ex) { throw new EJBException(ex); }
		catch (IOException ex)
		{
			log.error("Test search failed; perhaps no index yet?  Rebuilding.", ex);
			this.rebuild();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.search.IndexerManagement#start()
	 */

	@PostConstruct
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void start() throws Exception
	{
		log.info("Starting indexer");

		this.initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.search.IndexerManagement#stop()
	 */
	@PreDestroy
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void stop() throws Exception
	{
		log.info("Stopping indexer");
		getCurrentIndex().closeResources();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.search.IndexerManagement#rebuild()
	 */
	public void rebuild()
	{
		try
		{
			//get update lock first.
			updateLock.tryLock(1, TimeUnit.MINUTES);
			try
			{
				//try to get a lock, if we don't get it, another thread is already rebuilding!
				rebuildLock.tryLock(0,TimeUnit.SECONDS);

				log.info("Rebuilding all search indexes");

				IndexMgr mgr = getFallowIndex();

				Modifier mod = mgr.modifyIndex(true);

				log.info("Indexing all messages");
				this.indexAllMail(mod);
				mod.flush();
				log.info(mod.docCount() + " total documents in index");

				mod.close();

				this.swapCurrentIndex();
			}
			catch (InterruptedException e)
			{
				log.warn("An index rebuild is already going; skipping rebuild.", e);
			}
			catch (IOException ex) { throw new EJBException(ex); }
			finally 
			{
				if(rebuildLock.isHeldByCurrentThread()) rebuildLock.unlock();
			}
		}
		catch (InterruptedException e)
		{
			log.warn("Waited for update to finish but it didn't within 1m; cancelling rebuild.", e);
		}
		finally
		{
			if(updateLock.isHeldByCurrentThread()) updateLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.search.IndexerManagement#update()
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update()
	{
		try
		{
			//try to get a lock.
			updateLock.tryLock(1, TimeUnit.MILLISECONDS);
			log.debug("Updating search index");

			try
			{
				// We can use hibernate here since the cache is probably full
				// of the recent changes.  It's more convenient than raw SQL
				// because we need to make two passes across the data, one
				// for deletes and one for adds.

				IndexMgr mgr = getCurrentIndex();
				Date since = new Date(mgr.getLastModified());

				Modifier mod = mgr.modifyIndex(false);

				if (log.isDebugEnabled())
					log.debug("Fetching archived entries since " + since);

				List<Mail> indexMail = this.em.findMailSince(since);
				if (log.isDebugEnabled())
					log.debug(indexMail.size() + " messages to index");

				for (Mail m: indexMail)
				{
					try
					{
						SubEthaMessage msg = new SubEthaMessage(this.mailSession, m.getContent());
						mod.indexMail(m.getList().getId(), m.getId(), m.getFrom(), m.getSubject(), msg.getIndexableText());
					}
					catch (MessagingException ex)
					{
						log.error("Exception indexing message " + m.getId(), ex);
					}
				}

				mod.flush();

				if (log.isDebugEnabled())
					log.debug(mod.docCount() + " total documents in index");

				mod.close();
			}
			catch (IOException ex) { throw new EJBException(ex); }
		}
		catch (InterruptedException e)
		{
			log.warn("The index is currently being updated; skipping new update.");
		}
		finally
		{
			if (updateLock.isHeldByCurrentThread())
				updateLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.search.i.Indexer#search(java.lang.Long, java.lang.String, int, int)
	 */
	public SimpleResult search(Long listId, String queryText, int firstResult, int maxResults)
		throws SearchException
	{
		if (log.isDebugEnabled())
			log.debug("Searching list " + listId + " for text " + queryText);

		if (queryText == null || queryText.length() == 0)
		{
			throw new SearchException("Must enter some text to search for.");
		}
		try
		{
			return getCurrentIndex().search(listId, queryText, firstResult, maxResults);
		}
		catch (IOException ex) { throw new EJBException(ex); }
		catch (ParseException ex) { throw new SearchException(ex.getMessage()); }
	}

	/**
	 * Launches a sql statement that indexes all mail messages into the modifier.
	 * Avoids using hibernate so that the 2nd-level cache is not polluted with
	 * a ton of crap.
	 */
	protected void indexAllMail(Modifier modifier) throws IOException
	{
		try
		{
			Connection con = this.ds.getConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try
			{
				stmt = con.prepareStatement("select m.listId, m.id, m.senderNormal, m.subject, m.content from Mail m where m.hold is null");
				rs = stmt.executeQuery();

				while (rs.next())
				{
					Long listId = rs.getLong(1);
					Long id = rs.getLong(2);
					String from = rs.getString(3);
					String subject = rs.getString(4);
					Blob body = rs.getBlob(5);

					// Not quite sure how this is happening, but if it does, ignore the mail.
					if (body == null)
						continue;

					try
					{
						SubEthaMessage msg = new SubEthaMessage(this.mailSession, body.getBinaryStream());

						modifier.indexMail(listId, id, from, subject, msg.getIndexableText());
					}
					catch (Exception ex)
					{
						log.error("Exception while indexing message " + id, ex);
					}
				}
			}
			finally
			{
				try { if (stmt != null) stmt.close(); }
				finally { con.close(); }
			}
		}
		catch (SQLException ex) { throw new EJBException(ex); }
	}

}

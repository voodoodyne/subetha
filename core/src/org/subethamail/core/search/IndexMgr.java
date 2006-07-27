/*
 * $I90d: IndexerEJB.java 86 2006-02-22 03:36:01Z jeff $
 * $URL$
 */

package org.subethamail.core.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.Searcher;
import org.subethamail.core.search.i.SimpleHit;
import org.subethamail.core.search.i.SimpleResult;

/**
 * Abstraction of an index directory.  There will probably end up
 * being two of these.
 * 
 * @author Jeff Schnitzer
 */
public class IndexMgr
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(IndexMgr.class);
	
	/** */
	static final String INDEX_DIR_NAME = "index";
	static final String AGE_FILE_NAME = "age";
	
	/** 
	 * Amount of "slop" to consider when reindexing things.  This should
	 * be the maximum possible clock skew between the appservers.  If this
	 * is too large we will reindex things we don't have to, and if it is
	 * too small (ie clock skew too large) we risk missing updates.
	 */
	static final long SLOP_MILLIS = 1000 * 20;
	
	/** Delay for sleeping before deleting all files */
	static final long DELETE_DELAY_MILLIS = 1000 * 10;
	
	/** The search fields we always use */
	static final String[] SEARCH_FIELDS = new String[] { Modifier.FIELD_SUBJECT, Modifier.FIELD_BODY};

	/** The analyzer that we will use */
	static final Analyzer ANALYZER = new StandardAnalyzer();

	/** Path to the directory containing all files here */
	File base;

	/** The storage location for all the indexes */
	File indexDir;

	/** The file whose last modified date represents the last update time */
	File ageFile;
	
	/** Lazily created (and reset), access this through the synchronized access method */
	private IndexSearcher searcher;
	
	/**
	 */
	public IndexMgr(File base)
	{
		this.base = base;
		
		this.indexDir = new File(this.base, INDEX_DIR_NAME);
		this.ageFile = new File(this.base, AGE_FILE_NAME);
	}
	
	/**
	 * @returns the time at which the last index modification was begun.
	 */
	public long getLastModified()
	{
		return this.ageFile.lastModified() - SLOP_MILLIS;
	}
	
	/**
	 * Always access searcher through this method.
	 */
	synchronized Searcher getSearcher() throws IOException
	{
		if (this.searcher == null)
		{
		    this.searcher = new IndexSearcher(this.indexDir.getPath());
		}
		
		return this.searcher;
	}
	
	/**
	 * Called by the Modifier when closed.  Just resets the searcher.
	 */
	synchronized void closeResources() throws IOException
	{
		if (this.searcher != null)
		{
			this.searcher.close();
			this.searcher = null;
		}
	}
	
	/**
	 * Starts modification of the index by initializing the age file
	 * and creating a Modifier.  Caller is expected to close it!
	 */
	public Modifier modifyIndex(boolean truncate) throws IOException
	{
		this.indexDir.mkdirs();
		
		if (!this.ageFile.createNewFile())
			if (!this.ageFile.setLastModified(System.currentTimeMillis()))
				throw new EJBException("Unable to set age");
		
		IndexModifier actual = new IndexModifier(this.indexDir, ANALYZER, truncate); 

		return new Modifier(actual, this);
	}
	
	/**
	 * Searches this index.
	 */
	public SimpleResult search(Long listId, String queryText, int firstResult, int maxResults) throws IOException, ParseException
	{
		QueryParser parser = new MultiFieldQueryParser(SEARCH_FIELDS, ANALYZER);
	    Query query = parser.parse(queryText);
	    Filter filter = new RangeFilter(Modifier.FIELD_LIST_ID, listId.toString(), listId.toString(), true, true);
	    Hits hits = this.getSearcher().search(query, filter);

	    int total = hits.length();
	    
	    int returnCount = Math.min(maxResults, total - firstResult);
	    if (returnCount < 0)
	    	returnCount = 0;
	    
	    List<SimpleHit> searchHits = new ArrayList<SimpleHit>(returnCount);
	    
	    for (int i=firstResult; i<(firstResult+returnCount); i++)
	    	searchHits.add(this.toSearchHit(hits.doc(i), hits.score(i)));
	    
		return new SimpleResult(total, searchHits);
	}
	
	/**
	 * Converts a Document (as structured by Modifier) to a SearchHit
	 */
	SimpleHit toSearchHit(Document doc, float score)
	{
		String encId = doc.get(Modifier.FIELD_MAIL_ID);
		Long id = Long.valueOf(encId);

		return new SimpleHit(id, score);
	}
	
	/**
	 * Delays for 10 seconds and then deletes all contained files.
	 * This gives an opportunity for searches to complete.
	 */
	public void delayedDelete() throws IOException
	{
		// It would be nice to do this in a background thread but
		// we might run into problems with rapid iterative rebuilding
		// of the indexes when unit testing.
		
		try
		{
			Thread.sleep(DELETE_DELAY_MILLIS);
		}
		catch (InterruptedException ex) {}
		
		this.delete();
	}

	/**
	 * Deletes all file data associated with this index.
	 */
	public synchronized void delete() throws IOException
	{
		this.closeResources();
		deleteDir(this.base);
	}
	
	/**
	 * Utility method to recursively delete a directory
	 */
	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			for (File child: dir.listFiles())
			{
				if (!deleteDir(child))
					return false;
			}
		}

		return dir.delete();
	}
}


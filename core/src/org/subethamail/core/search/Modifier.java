/*
 * $I90d: IndexerEJB.java 86 2006-02-22 03:36:01Z jeff $
 * $URL$
 */

package org.subethamail.core.search;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexModifier;
import org.apache.lucene.index.Term;

/**
 * Facade for an IndexModifier that provides a more sane view.
 * 
 * Documents have an id, a subject, and a body.
 * 
 * @author Jeff Schnitzer
 */
public class Modifier
{
	/** */
	private static Log log = LogFactory.getLog(Modifier.class);
	
	/** */
	public static final String FIELD_LIST_ID = "listId";
	public static final String FIELD_MAIL_ID = "mailId";
	public static final String FIELD_SUBJECT = "subject";
	public static final String FIELD_BODY = "body";
	
	/** If a field is shorter than this, ignore it */
	public static final int MIN_USEFUL_FIELD_LEN = 3;
	
	/** */
	IndexModifier actual;
	
	/** A reference to our parent */
	IndexMgr owner;
	
	/**
	 */
	public Modifier(IndexModifier actual, IndexMgr owner)
	{
		this.actual = actual;
		this.owner = owner;
	}
	
	/**
	 * Indexes a message into the modifier.
	 */
	void indexMail(Long listId, Long mailId, String subject, String body) throws IOException
	{
		if (log.isTraceEnabled())
			log.trace("Indexing message " + mailId + "/" + subject);
			
		if (subject.length() < MIN_USEFUL_FIELD_LEN && body.length() <= MIN_USEFUL_FIELD_LEN)
			return;
			
		Document doc = new Document();
		doc.add(new Field(FIELD_LIST_ID, listId.toString(), Field.Store.YES, Field.Index.NO_NORMS));
		doc.add(new Field(FIELD_MAIL_ID, mailId.toString(), Field.Store.YES, Field.Index.NO_NORMS));
		
		if (subject.length() >= MIN_USEFUL_FIELD_LEN)
			doc.add(new Field(FIELD_SUBJECT, subject, Field.Store.NO, Field.Index.TOKENIZED));
		
		if (body.length() >= MIN_USEFUL_FIELD_LEN)
			doc.add(new Field(FIELD_BODY, body, Field.Store.NO, Field.Index.TOKENIZED));
		
		this.actual.addDocument(doc);
	}
	
	/**
	 * Deletes a message from the index.
	 */
	void deleteMessage(Long id) throws IOException
	{
		if (log.isTraceEnabled())
			log.trace("Deleting message from index with id " + id);
		
		Term term = new Term(FIELD_MAIL_ID, id.toString());
		
		this.actual.deleteDocuments(term);
	}

	/**
	 * @see IndexModifier#flush() 
	 */
	public void flush() throws IOException
	{
		this.actual.flush();
	}
	
	/**
	 * @see IndexModifier#close() 
	 */
	public void close() throws IOException
	{
		this.owner.closeResources();
		this.actual.optimize();
		this.actual.close();
	}
	
	/**
	 * @see IndexModifier#docCount()
	 */
	public int docCount()
	{
		return this.actual.docCount();
	}
}


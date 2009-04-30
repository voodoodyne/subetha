/*
 * $Id: Indexer.java 749 2006-08-21 21:59:29Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/search/i/Indexer.java $
 */

package org.subethamail.core.search.i;

import javax.ejb.Local;

import org.subethamail.common.SearchException;

/**
 * Interface for indexing and searching through textual data.  This is
 * a very primitive interface that only returns the ids of data objects.
 * It is designed to be serviced remotely.  For user purposes, a method
 * that returns data objects about the hits will be more useful.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Indexer
{
	/** */
	public static final String JNDI_NAME = "subetha/IndexerBean/local";

	/**
	 * Queries the index for a specific mailing list.
	 * 
	 * @param firstResult is the 0-based index of the first result to fetch
	 * @param maxResults is the number of results to return.
	 */
	public SimpleResult search(Long listId, String queryText, int firstResult, int maxResults)
		throws SearchException;
	
	/**
	 * Updates the index with any changes in since the last update.
	 */
	public void update();
}


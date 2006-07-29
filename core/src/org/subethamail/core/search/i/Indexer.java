/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.search.i;

import javax.ejb.Remote;

/**
 * Interface for indexing and searching through textual data.  This is
 * a very primitive interface that only returns the ids of data objects.
 * It is designed to be serviced remotely.  For user purposes, a method
 * that returns data objects about the hits will be more useful.
 *
 * @author Jeff Schnitzer
 */
@Remote
public interface Indexer
{
	/** */
	public static final String JNDI_NAME = "subetha/IndexerBean/remote";

	/**
	 * Queries the index for a specific mailing list.
	 * 
	 * @param firstResult is the 0-based index of the first result to fetch
	 * @param maxResults is the number of results to return.
	 */
	public SimpleResult search(Long listId, String queryText, int firstResult, int maxResults);
	
	/**
	 * Updates the index with any changes in since the last update.
	 */
	public void update();
}


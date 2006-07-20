/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.search.i;

import javax.ejb.Local;

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
	 * Queries the index.
	 * 
	 * @param firstResult is the 0-based index of the first result to fetch
	 * @param maxResults is the number of results to return.
	 */
	public SimpleResult search(String queryText, int firstResult, int maxResults);
}


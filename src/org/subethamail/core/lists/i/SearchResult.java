/*
 * $Id: SearchResult.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/SearchResult.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.List;


/**
 * Results of a fulltext search.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class SearchResult implements Serializable
{
	/** */
	int total;
	List<SearchHit> hits;
	
	protected SearchResult()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/** */
	public SearchResult(int total, List<SearchHit> hits)
	{
		this.total = total;
		this.hits = hits;
	}

	/** */
	public List<SearchHit> getHits()
	{
		return this.hits;
	}

	/** */
	public int getTotal()
	{
		return this.total;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {total=" + this.total + ", hits=" + this.hits + "}";
	}
}

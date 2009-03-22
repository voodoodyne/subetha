/*
 * $Id: SimpleResult.java 684 2006-07-20 11:49:42Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/search/i/SimpleResult.java $
 */

package org.subethamail.core.search.i;

import java.io.Serializable;
import java.util.List;


/**
 * Results of a fulltext search.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class SimpleResult implements Serializable
{
	/** */
	int total;
	List<SimpleHit> hits;
	
	/** */
	public SimpleResult(int total, List<SimpleHit> hits)
	{
		this.total = total;
		this.hits = hits;
	}

	/** */
	public List<SimpleHit> getHits()
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
		return "SimpleResult {total=" + this.total + ", hits=" + this.hits + "}";
	}
}

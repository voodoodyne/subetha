/*
 * $Id: SimpleHit.java 684 2006-07-20 11:49:42Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/search/i/SimpleHit.java $
 */

package org.subethamail.core.search.i;

import java.io.Serializable;


/**
 * A single hit from a full text search.  Only provides
 * id information.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class SimpleHit implements Serializable
{
	/** */
	Long id;
	float score;
	
	/** */
	public SimpleHit(Long id, float score)
	{
		this.id = id;
		this.score = score;
	}

	/** */
	public Long getId()
	{
		return this.id;
	}

	/** */
	public float getScore()
	{
		return this.score;
	}
	
	/** */
	public String toString()
	{
		return "SimpleHit {id=" + this.id + ", score=" + this.score + "}";
	}
}

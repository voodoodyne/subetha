/*
 * $Id: SearchHit.java 749 2006-08-21 21:59:29Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/SearchHit.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.Date;


/**
 * A single hit from a full text search.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class SearchHit implements Serializable
{
	Long id;
	String subject;
	String fromEmail;	// might be null if no permission to view
	String fromName;
	Date sentDate;

	/** Needed by Hessian */
	protected SearchHit() {}
	
	/**
	 */
	public SearchHit(
			Long id,
			String subject,
			String fromEmail,
			String fromName,
			Date sentDate)
	{
		this.id = id;
		this.subject = subject;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.sentDate = sentDate;
	}
	
	/** */
	public Long getId()
	{
		return this.id;
	}

	/** */
	public String getSubject()
	{
		return this.subject;
	}

	/**
	 * @return null if the client does not have permission to view emails. 
	 */
	public String getFromEmail()
	{
		return this.fromEmail;
	}
	
	/** */
	public String getFromName()
	{
		return this.fromName;
	}

	/** */
	public Date getSentDate()
	{
		return this.sentDate;
	}

	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + "}";
	}
}

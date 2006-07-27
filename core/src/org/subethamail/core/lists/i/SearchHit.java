/*
 * $Id$
 * $URL$
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
	float score;

	/**
	 */
	public SearchHit(
			Long id,
			String subject,
			String fromEmail,
			String fromName,
			Date sentDate,
			float score)
	{
		this.id = id;
		this.subject = subject;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.sentDate = sentDate;
		this.score = score;
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
	public float getScore()
	{
		return this.score;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + ", score=" + this.score + "}";
	}
}

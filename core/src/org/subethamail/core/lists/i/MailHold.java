/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.Date;

/**
 * Data about a held message.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MailHold implements Serializable
{	
	Long id;
	String subject;
	String from;
	Date date;
	boolean hard;

	/**
	 */
	public MailHold(
			Long id,
			String subject,
			String from,
			Date date,
			boolean hard)
	{
		this.id = id;
		this.subject = subject;
		this.from = from;
		this.date = date;
		this.hard = hard;
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
	 */
	public String getFrom()
	{
		return this.from;
	}
	
	/** */
	public Date getDate()
	{
		return this.date;
	}

	/** */
	public boolean isHard()
	{
		return this.hard;
	}
}

/*
 * $Id: MailHold.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/MailHold.java $
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

	protected MailHold()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

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

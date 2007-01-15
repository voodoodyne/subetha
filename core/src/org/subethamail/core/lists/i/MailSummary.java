/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The summary information about a piece of mail, suitable for display
 * on a page of threads.  The mail body is not included, but the
 * relationships to other mail messages in the thread hierarchy are.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MailSummary implements Serializable
{	
	Long id;
	Long listId;
	String subject;
	String fromEmail;	// might be null if no permission to view
	String fromName;
	Date sentDate;
	List<MailSummary> replies;

	/**
	 */
	public MailSummary(
			Long id,
			Long listId,
			String subject,
			String fromEmail,
			String fromName,
			Date sentDate,
			List<MailSummary> replies)
	{
		this.id = id;
		this.listId = listId;
		this.subject = subject;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.sentDate = sentDate;
		this.replies = replies;
	}
	
	/** */
	public Long getId()
	{
		return this.id;
	}
	
	/** */
	public Long getListId()
	{
		return this.listId;
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
	public List<MailSummary> getReplies()
	{
		return this.replies;
	}
}

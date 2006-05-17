/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.util.Date;
import java.util.List;

/**
 * Adds the mail body and thread root to a mail summary.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MailData extends MailSummary
{
	/** */
	Long listId;
	List<String> textParts;
	List<AttachmentData> attachments;
	MailSummary threadRoot;
	
	
	/**
	 */
	public MailData(
			Long id,
			String subject,
			String fromEmail,
			String fromName,
			Date dateCreated,
			List<MailSummary> replies,
			Long listId,
			List<String> textParts,
			List<AttachmentData> attachments)
	{
		super(id, subject, fromEmail, fromName, dateCreated, replies);
		
		this.listId = listId;
		this.textParts = textParts;
		this.attachments = attachments;
	}

	/**
	 * @return the id of the mailing list in which this mail lives. 
	 */
	public Long getListId()
	{
		return this.listId;
	}

	/**
	 * All the textual components of the message, in flattened form.
	 */
	public List<String> getTextParts()
	{
		return this.textParts;
	}

	/**
	 * 
	 * @return the attachments
	 */
	public List<AttachmentData> getAttachments()
	{
		return this.attachments;
	}
	
	/**
	 * @return the root of the thread hierarchy in which this message
	 *  participates.  This object will be part of the hierarchy. 
	 */
	public MailSummary getThreadRoot()
	{
		return this.threadRoot;
	}

	/**
	 * We need a setter because we need to exist so that we can be
	 * inserted in the thread history that gets set here.
	 */
	public void setThreadRoot(MailSummary threadRoot)
	{
		this.threadRoot = threadRoot;
	}

}

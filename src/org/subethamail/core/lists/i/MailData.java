/*
 * $Id: MailData.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/MailData.java $
 */

package org.subethamail.core.lists.i;

import java.util.ArrayList;
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
	List<InlinePartData> inlineParts;
	List<TextPartData> textParts;
	List<AttachmentPartData> attachmentParts;
	MailSummary threadRoot;
	
	protected MailData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}
	
	/**
	 */
	public MailData(
			Long id,
			String subject,
			String fromEmail,
			String fromName,
			Date sentDate,
			List<MailSummary> replies,
			Long listId,
			List<InlinePartData> inlineParts,
			List<AttachmentPartData> attachmentParts 
			)
	{
		super(id, listId, subject, fromEmail, fromName, sentDate, replies);
		
		this.listId = listId;
		this.inlineParts = inlineParts;
		this.attachmentParts = attachmentParts;
		
		this.textParts = new ArrayList<TextPartData>(inlineParts.size());	
		for (InlinePartData inline : inlineParts) 
			if (inline instanceof TextPartData) textParts.add((TextPartData)inline); 
	}

	/**
	 * @return the id of the mailing list in which this mail lives. 
	 */
	public Long getListId()
	{
		return this.listId;
	}

	/**
	 * All the textual components of the message.
	 */
	public List<TextPartData> getTextParts()
	{
		return this.textParts;
	}

	/**
	 * All the inline components of the message.
	 */
	public List<InlinePartData> getInlineParts()
	{
		return this.inlineParts;
	}

	/**
	 * All the attachments saved outside the mail.
	 */
	public List<AttachmentPartData> getAttachments()
	{
		return this.attachmentParts;
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

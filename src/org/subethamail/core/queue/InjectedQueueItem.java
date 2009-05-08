package org.subethamail.core.queue;

import java.io.Serializable;

import org.subethamail.entity.Mail;

public class InjectedQueueItem implements Serializable
{
	/**
	 */
	private static final long serialVersionUID = 1L;

	protected Long mailId;
	
	public InjectedQueueItem(Mail mail)
	{
		this.mailId = mail.getId();
	}
	
	public Long getMailId() { return this.mailId; }
}

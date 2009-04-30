package org.subethamail.core.queue;

import java.io.Serializable;

public class MailDelivery implements Serializable
{
	/**
	 */
	private static final long serialVersionUID = 1L;

	protected Long personId;
	protected Long mailId;
	
	public MailDelivery(Long mail, Long person)
	{
		this.personId = person;
		this.mailId = mail;
	}
	
	public Long getMailId() { return this.mailId; }
	public Long getPersonId() { return this.personId; }
}

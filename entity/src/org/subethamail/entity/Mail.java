/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.MailUtils;
import org.subethamail.common.valid.Validator;

/**
 * Entity for a single piece of mail.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Mail implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Mail.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_MAIL_CONTENT)
	byte[] content;
	
	/** Message id might not exist */
	@Column(nullable=true, length=Validator.MAX_MAIL_MESSAGE_ID)
	String messageId;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_MAIL_SUBJECT)
	String subject;
	
	/** TODO: should we track versions with and without email addresses? */
	@Column(nullable=true, length=Validator.MAX_MAIL_FROM)
	String from;
	
	/** Date the entity was created, not from header fields */
	@Column(nullable=false)
	Date dateCreated;
	
	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList mailingList;
	
	/** */
	@ManyToOne
	@JoinColumn(name="parentId", nullable=true)
	Mail parent;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	SortedSet<Mail> replies;
	
	/**
	 */
	public Mail() {}
	
	/**
	 */
	public Mail(Message msg, MailingList list, Mail parent) throws MessagingException, IOException
	{
		if (log.isDebugEnabled())
			log.debug("Creating new mail");
		
		this.dateCreated = new Date();
		this.mailingList = list;
		this.parent = parent;
		
		ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
		msg.writeTo(tmpStream);
		
		// These are validated normally.
		this.setContent(tmpStream.toByteArray());
		this.setSubject(msg.getSubject());
		this.setMessageId(MailUtils.getMessageId(msg));
		this.setFrom(MailUtils.getFrom(msg));
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/**
	 */
	public byte[] getContent() { return this.content; }
	
	/**
	 */
	public void setContent(byte[] value)
	{
		if (value == null || value.length > Validator.MAX_MAIL_CONTENT)
			throw new IllegalArgumentException("Invalid content");

		if (log.isDebugEnabled())
			log.debug("Setting content of " + this);
		
		this.content = value;
	}

	/**
	 */
	public String getMessageId() { return this.messageId; }
	
	/**
	 */
	public void setMessageId(String value)
	{
		if (value != null && value.length() > Validator.MAX_MAIL_MESSAGE_ID)
			throw new IllegalArgumentException("Invalid message id");

		if (log.isDebugEnabled())
			log.debug("Setting message id of " + this + " to " + value);
		
		this.messageId = value;
	}

	/**
	 */
	public String getSubject() { return this.subject; }
	
	/**
	 */
	public void setSubject(String value)
	{
		if (value == null || value.length() > Validator.MAX_MAIL_SUBJECT)
			throw new IllegalArgumentException("Invalid subject");

		if (log.isDebugEnabled())
			log.debug("Setting subject of " + this + " to " + value);
		
		this.subject = value;
	}

	/**
	 */
	public String getFrom() { return this.from; }
	
	/**
	 */
	public void setFrom(String value)
	{
		if (value != null && value.length() > Validator.MAX_MAIL_FROM)
			throw new IllegalArgumentException("Invalid from");

		if (log.isDebugEnabled())
			log.debug("Setting from of " + this + " to " + value);
		
		this.from = value;
	}
	
	/** Note no setter; this is the date the mail came into the system */
	public Date getDateCreated() { return this.dateCreated; }

	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", subject=" + this.subject + "}";
	}

	/**
	 * Natural sort order is based on creation date
	 */
	public int compareTo(Object arg0)
	{
		Mail other = (Mail)arg0;

		return this.dateCreated.compareTo(other.getDateCreated());
	}
}


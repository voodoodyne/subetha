/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ejb.EJBException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Table;
import org.hibernate.validator.Email;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.common.valid.Validator;

/**
 * Entity for a single piece of mail.
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
	@NamedQuery(
		name="MailByMessageId", 
		query="from Mail m where m.list.id = :listId and m.messageId = :messageId",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
			name="MailByList", 
			query="from Mail m where m.list.id = :listId order by m.dateCreated desc",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		),
	@NamedQuery(
		name="WantsReferenceToMessageId", 
		query="select m from Mail as m join fetch m.wantedReference as ref where ref = :messageId and m.list.id = :listId",
		hints={
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
@Table(
	name="Mail",
	indexes={@Index(name="mailMessageIdIndex", columnNames={"listId", "messageId"})}
)
public class Mail implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Mail.class);
	
	/**
	 * Possible moderation states 
	 */
	public enum HoldType
	{
		SELF,		// Hold for self-approval
		MODERATOR	// Hold for list moderator to approve
	}
	
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
	
	/** rfc222-style, should be taken from the envelope sender */
	@Email
	@Column(name="fromField", nullable=true, length=Validator.MAX_MAIL_FROM)
	String from;
	
	/** normalized to box@domain.tld (no "personal" part) */
	@Email
	@Column(nullable=true, length=Validator.MAX_MAIL_FROM)
	@Index(name="mailFromNormalIndex")
	String fromNormal;
	
	/** Date the entity was created, not from header fields */
	@Column(nullable=false)
	Date dateCreated;
	
	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList list;
	
	/** */
	@ManyToOne
	@JoinColumn(name="parentId", nullable=true)
	Mail parent;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
	@Sort(type=SortType.NATURAL)
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	SortedSet<Mail> replies;
	
	/** 
	 * This represents a list of references we are more interested
	 * in using as our parent than what we have currently.  The first
	 * entry is the best possible. 
	 */
	@CollectionOfElements
	@JoinTable(name="WantedReference", joinColumns={@JoinColumn(name="mailId")})
	@Column(name="messageId", nullable=false)
	@IndexColumn(name="ord")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	@Index(name="mailWantedRefIndex")	// TODO:  this doesn't seem to work
	List<String> wantedReference;
	
	/** 
	 * Is it held for moderation?  If null, no need for moderation.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable=true)
	@Index(name="holdIndex")
	HoldType hold;
	
	/**
	 */
	public Mail() {}
	
	/**
	 * @param holdFor can be null which means none required
	 */
	public Mail(InternetAddress from, SubEthaMessage msg, MailingList list, HoldType holdFor) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Creating new mail");
		
		this.dateCreated = new Date();
		this.list = list;
		this.hold = holdFor;
		
		byte[] raw;
		try
		{
			ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
			msg.writeTo(tmpStream);
			raw = tmpStream.toByteArray();
		}
		catch (IOException ex) { throw new EJBException(ex); }
		
		// These are validated normally.
		this.setContent(raw);
		this.setSubject(msg.getSubject());
		this.setMessageId(msg.getMessageID());
		this.setFrom(from.toString());
		this.setFromNormal(from.getAddress());
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
		this.content = value;
	}

	/**
	 */
	public String getMessageId() { return this.messageId; }
	
	public void setMessageId(String value)
	{
		this.messageId = value;
	}
	
	/**
	 */
	public String getSubject() { return this.subject; }
	
	public void setSubject(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting subject of " + this + " to " + value);
		
		this.subject = value;
	}

	/**
	 * @return an rfc222-compilant address list, comma separated.  It will
	 * be parseable with InternetAddress.parse().
	 */
	public String getFrom() { return this.from; }
	
	/**
	 * @param value can be null.
	 */
	public void setFrom(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting from of " + this + " to " + value);
		
		this.from = value;
	}
	
	/** 
	 * Convenient way of getting the javamail object. 
	 */
	public InternetAddress getFromAddress()
	{
		try
		{
			return new InternetAddress(this.getFrom());
		}
		catch (AddressException ex)
		{
			// Should be impossible because we were created with a valid InternetAddress
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * @return the normalized box@domain.tld for the first From entry.
	 *
	 * @see Validator#normalizeEmail(String)
	 */
	public String getFromNormal() { return this.fromNormal; }
	
	/**
	 * @param value does not need to be normalized already; it
	 *  will be normalized in this method.  Null is ok.
	 */
	public void setFromNormal(String value)
	{
		value = Validator.normalizeEmail(value);

		if (log.isDebugEnabled())
			log.debug("Setting fromNormal of " + this + " to " + value);
		
		this.fromNormal = value;
	}
	
	/** Note no setter; this is the date the mail came into the system */
	public Date getDateCreated() { return this.dateCreated; }
	
	/**
	 */
	public HoldType getHold() { return this.hold; }
	
	/**
	 * Releases a moderation hold
	 */
	public void release()
	{
		this.hold = null;
	}
	
	/** */
	public Mail getParent() { return this.parent; }
	
	public void setParent(Mail value)
	{
		this.parent = value;
	}
	
	/** */
	public Set<Mail> getReplies() { return this.replies; }

	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", subject=" + this.subject + "}";
	}
	
	/** */
	public MailingList getList() { return this.list; }

	/** */
	public List<String> getWantedReference() { return this.wantedReference; }
	
	public void setWantedReference(List<String> value)
	{
		this.wantedReference = value;
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


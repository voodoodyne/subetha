/*
 * $Id: Mail.java 1002 2009-03-20 01:18:14Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/Mail.java $
 */

package org.subethamail.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Table;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.common.TimeUtils;
import org.subethamail.entity.i.Validator;

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
		query="from Mail m where m.list.id = :listId and m.hold is null order by m.sentDate desc",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
			name="CountMailByList",
			query="select count(*) from Mail m where m.list.id = :listId and m.hold is null",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
	),
	@NamedQuery(
		name="HeldMail",
		query="from Mail m where m.list.id = :listId and m.hold is not null order by m.sentDate desc",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="HeldMailCount",
		query="select count(*) from Mail m where m.list.id = :listId and m.hold is not null",
		hints={
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="WantsReferenceToMessageId",
		query="select m from Mail as m join fetch m.wantedReference as ref where ref = :messageId and m.list.id = :listId",
		hints={
		}
	),
	@NamedQuery(
		name="SoftHoldsByPerson",
		query="select m from Mail m, EmailAddress email where email.person.id = :personId and email.id = m.senderNormal and m.hold = 'SOFT'",
		hints={
		}
	),
	@NamedQuery(
		name="HeldMailOlderThan",
		query="select m from Mail m where m.hold is not null and m.arrivalDate < :cutoff",
		hints={
		}
	),
	@NamedQuery(
		name="HeldMailFrom",
		query="select m from Mail m where m.hold is not null and m.senderNormal = :sender and m.id <> :excluding order by m.arrivalDate desc",
		hints={
		}
	),
	@NamedQuery(
		name="CountRecentHeldMailFrom",
		query="select count(*) from Mail m where m.hold is not null and m.senderNormal = :sender and m.arrivalDate > :since",
		hints={
		}
	),
	@NamedQuery(
		name="RecentMailBySubject",
		query="select m from Mail m where m.hold is null and m.list.id = :listId and m.subject = :subject and m.arrivalDate >= :cutoff order by m.arrivalDate desc",
		hints={
		}
	),
	@NamedQuery(
		name="MailSince",
		query="select m from Mail m where m.hold is null and m.arrivalDate > :since",
		hints={
		}
	),
	@NamedQuery(
		name="CountMail",
		query="select count(*) from Mail",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
@Table(
	appliesTo="Mail",
	indexes={@Index(name="mailMessageIdIndex", columnNames={"listId", "messageId"})}
)
@Indexed
public class Mail implements Serializable, Comparable<Mail>
{
	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(Mail.class);

	/**
	 * Possible moderation states
	 */
	public enum HoldType
	{
		SOFT,	// Can be flushed if msg becomes associated with someone who has Permission.POST
		HARD	// Must be manually approved no matter what
	}

	/** */
	@Id
	@GeneratedValue
	Long id;

	/** Nullable because content is set after object persistence */
	@Column(nullable=true, length=Validator.MAX_MAIL_CONTENT)
	@Field
	@FieldBridge(impl=MessageContentBridge.class)
	byte[] content;

	/** Message id might not exist */
	@Column(nullable=true, length=Validator.MAX_MAIL_MESSAGE_ID)
	String messageId;

	/** */
	@Column(nullable=false, length=Validator.MAX_MAIL_SUBJECT)
	@Index(name="subject")
	@Field(boost=@Boost(5f))
	String subject;

	/**
	 * rfc222-style version of the sender.  This is pretty, includes the
	 * person's name.  Note that it is not usually the envelope sender; it will
	 * be the Sender: field if it exists, the first From: element if it exists,
	 * and last of all the envelope sender.
	 * 
	 * Don't try to use hibernate email validator, it doesn't understand
	 * the rfc222 style with personal names.
	 */
	@Column(nullable=true, length=Validator.MAX_MAIL_SENDER)
	String sender;

	/** 
	 * This field is the normalized interpretation of the sender.  Mostly this gets
	 * used to match for auto self moderation.
	 */
//	@Email
	// The validator failed on this address: SRS0=aHFE=YF=pobox.com=fredx@bounce2.pobox.com
	// It looks valid to me, so I can only guess that the validation pattern is broken.
	@Column(nullable=false, length=Validator.MAX_MAIL_SENDER)
	@Index(name="senderIndex")
	String senderNormal;

	/** Date the entity was created, not from header fields */
	@Column(nullable=false)
	@Index(name="arrivalDateIdx")
	Date arrivalDate;

	/** Date from the header fields, or the arrivalDate if there was no header */
	@Column(nullable=false)
	@Index(name="sentDateIdx")
	Date sentDate;

	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	@Field
	@FieldBridge(impl=MailingListBridge.class)
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

	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="mail")
	// Disabled caching until instrumentation problem with jboss-4.0.4.GA resolved
	//@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Set<Attachment> attachments;

	/**
	 * This represents a list of references we are more interested
	 * in using as our parent than what we have currently.  The first
	 * entry is the best possible.
	 */
	@ElementCollection
	@JoinTable(name="WantedReference", joinColumns={@JoinColumn(name="mailId")})
	@Column(name="messageId", nullable=false)
	@IndexColumn(name="ord")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	//@Index(name="mailWantedRefIndex")	// TODO:  this doesn't seem to work
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
	 * Pulls the date out of the message.
	 */
	public Mail(String envelopeSender, SubEthaMessage msg, MailingList list, HoldType holdFor) throws MessagingException
	{
		this(envelopeSender, msg, list, holdFor, msg.getSentDate());
	}

	/**
	 * Creates a new Mail object.  DOES NOT SET THE CONTENT.
	 *
	 * Content must be set separately because the detacher requires the mail object
	 * to exist before it can create Attachment objects, and the assigned content
	 * must contain the newly created ids of the attachments.
	 *
	 * @param envelopeSender can be null if no envelope sender was specified
	 * @param holdFor can be null which means none required
	 * @param sentDate is the date which should be used as the sent date.  If null, current time is chosen.
	 */
	public Mail(String envelopeSender, SubEthaMessage msg, MailingList list, HoldType holdFor, Date sentDate) throws MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Creating new mail");

		this.arrivalDate = new Timestamp(System.currentTimeMillis());

		this.sentDate = sentDate;
		if (this.sentDate == null)
			this.sentDate = this.arrivalDate;

		this.list = list;
		this.hold = holdFor;

		this.subject = msg.getSubject();
		if (this.subject == null)
			this.subject = "";

		this.messageId = msg.getMessageID();

		// Convoluted process to determine sender.
		// Check, in order:  Sender field, first entry of From field, envelope sender
		InternetAddress senderField = msg.getSenderWithFallback(envelopeSender);
		if (senderField == null)
			this.setSender("");
		else
			this.setSender(senderField.toString());

		this.replies = new TreeSet<Mail>();
		this.attachments = new HashSet<Attachment>();
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
	 * Convenience method
	 */
	public void setContent(SubEthaMessage msg) throws MessagingException
	{
		// TODO:  optimize this to do less memory copying

		byte[] raw;

		try
		{
			ByteArrayOutputStream tmpStream = new ByteArrayOutputStream(8192);
			msg.writeTo(tmpStream);
			raw = tmpStream.toByteArray();
		}
		catch (IOException ex) { throw new EJBException(ex); }

		this.setContent(raw);
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
	 * @return a single rfc222-compilant address.  It will
	 * be parseable with InternetAddress.parse().  This is
	 * just a pretty alias for getSender().
	 */
	public String getFrom()
	{
		return this.sender;
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
	 * @return the rfc822 version of our understanding of who sent the message
	 */
	public String getSender() { return this.sender; }

	/**
	 * @return the normalized (just box@domain.tld) version of the sender
	 *
	 * @see Validator#normalizeEmail(String)
	 */
	public String getSenderNormal() { return this.senderNormal; }

	/**
	 * @param value is the rfc822 value of who we understand is the sender of this message.
	 * @throws AddressException if the value is a bad address
	 */
	public void setSender(String value) throws AddressException
	{
		if (log.isDebugEnabled())
			log.debug("Setting sender of " + this + " to " + value);

		this.sender = value;
		
		InternetAddress addy = new InternetAddress(value);
		this.senderNormal = Validator.normalizeEmail(addy.getAddress());
	}

	/** This is the date the mail came into the system */
	public Date getArrivalDate() { return this.arrivalDate; }

	/** This is the "natural" date of the mail, from the header */
	public Date getSentDate() { return this.sentDate; }

	public void setSentDate(Date value)
	{
		this.sentDate = value;
	}

	/**
	 */
	public HoldType getHold() { return this.hold; }

	/**
	 * Releases a moderation hold
	 */
	public void approve()
	{
		this.hold = null;
	}

	/** */
	public Mail getParent() { return this.parent; }

	public void setParent(Mail value)
	{
		if (value.getId().equals(this.getId()))
			throw new IllegalArgumentException("Parent cannot be self!");

		this.parent = value;
	}

	/** */
	public Set<Mail> getReplies() { return this.replies; }

	/**
	 * Gets every message related to this one, or any reply.
	 * @return A list of the children, their children and so forth.
	 */
	public List<Mail> getDescendents()
	{
		Set<Mail> children = this.getReplies();

		if (children == null || children.size() < 1) return null;

		List<Mail> descendents = new Vector<Mail>(children.size());
		for (Object element : children)
		{
			Mail child = (Mail) element;
			List<Mail> underlings = child.getDescendents();
			if(underlings != null && underlings.size() > 0)
				descendents.addAll(underlings);
		}
		return descendents;
	}

	/** */
	@Override
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

	/** */
	public Set<Attachment> getAttachments() { return this.attachments; }

	/**
	 * Natural sort order is based on sent date, but we need
	 * to make sure that we don't return equal if we aren't.
	 */
	public int compareTo(Mail other)
	{
		int result = TimeUtils.compareDates(other.getSentDate(), this.sentDate);
		if (result == 0)
			return other.id.compareTo(this.id);
		else
			return result;
	}
}
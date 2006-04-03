/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * Each person's membership in a mailing list is represented by
 * a subscription entity which defines the role of that user. 
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Subscription implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Subscription.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@ManyToOne
	@JoinColumn(name="personId", nullable=false)
	Person person;
	
	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList mailingList;
	
	/**
	 * A value of null means that mail should not be delivered. 
	 */
	@ManyToOne
	@JoinColumn(name="deliverToId", nullable=true)
	EmailAddress deliverTo;
	
	/** 
	 * The role of this subscription.  Null indicates the Owner role.
	 * The reason for choosing null is so that if we ever expand the
	 * permission list, existing owners automatically get the perms. 
	 */
	@OneToOne
	@JoinColumn(name="roleId", nullable=true)
	Role role;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_SUBSCRIPTION_NOTE)
	String note;
	
	/**
	 */
	public Subscription() {}
	
	/**
	 */
	public Subscription(Person person, MailingList list, EmailAddress deliverTo, Role role)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new Subscription");
		
		this.person = person;
		this.mailingList = list;
		
		// This involves some validation
		this.setDeliverTo(deliverTo);
		this.setRole(role);
		
		// Notes should always start out empty
		this.note = "";
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public Person getPerson() { return this.person; }
	
	/** */
	public MailingList getMailingList() { return this.mailingList; }
	
	/**
	 * A value of null indicates that mail should not be delivered. 
	 */
	public EmailAddress getDeliverTo() { return this.deliverTo; }
	
	public void setDeliverTo(EmailAddress value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting deliverTo to " + value);
		
		if (value != null && !this.person.getEmailAddresses().containsKey(value.getId()))
			throw new IllegalArgumentException("Email address does not belong to the correct user");
			
		this.deliverTo = value;
	}
	
	/**
	 * A value of null indicates the special Owner role 
	 */
	public Role getRole() { return this.role; }
	
	public void setRole(Role value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting role to " + value);
		
		if (!this.mailingList.isValidRole(value))
			throw new IllegalArgumentException("Role does not belong to the correct list");
			
		this.role = value;
	}
	
	/**
	 * A private administrative note.
	 */
	public String getNote() { return this.note; }
	
	public void setNote(String value)
	{
		if (value == null || value.length() > Validator.MAX_SUBSCRIPTION_NOTE)
			throw new IllegalArgumentException("Invalid note");

		if (log.isDebugEnabled())
			log.debug("Setting note of " + this + " to " + value);
		
		this.note = value;
	}
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + "}";
	}

	/**
	 * Natural sort order is based on id?
	 */
	public int compareTo(Object arg0)
	{
		Subscription other = (Subscription)arg0;

		return this.id.compareTo(other.getId());
	}
}


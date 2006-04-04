/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.Permission;
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
	SubscriptionPK pk;
	
	/** This "overlaps" with the PK */
	@ManyToOne
	@JoinColumn(name="personId", insertable=false, updatable=false)
	Person person;
	
	/** This "overlaps" with the PK */
	@ManyToOne
	@JoinColumn(name="listId", insertable=false, updatable=false)
	MailingList list;
	
	/**
	 * A value of null means that mail should not be delivered. 
	 */
	@ManyToOne
	@JoinColumn(name="deliverToId", nullable=true)
	EmailAddress deliverTo;
	
	/** 
	 * The role of this subscription.
	 */
	@OneToOne
	@JoinColumn(name="roleId", nullable=false)
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
		
		this.pk = new SubscriptionPK(person.getId(), list.getId());
		
		this.person = person;
		this.list = list;
		
		// This involves some validation
		this.setDeliverTo(deliverTo);
		this.setRole(role);
		
		// Notes should always start out empty
		this.note = "";
	}
	
	/** */
	public SubscriptionPK getPk()		{ return this.pk; }

	/** */
	public Person getPerson() { return this.person; }
	
	/** */
	public MailingList getList() { return this.list; }
	
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
	 */
	public Role getRole() { return this.role; }
	
	public void setRole(Role value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting role to " + value);
		
		if (!this.list.isValidRole(value))
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
		return this.getClass() + " {pk=" + this.pk + "}";
	}

	/**
	 * Natural sort order is based list
	 */
	public int compareTo(Object arg0)
	{
		Subscription other = (Subscription)arg0;

		return this.list.compareTo(other.getList());
	}
}


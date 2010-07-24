/*
 * $Id: SubscriptionBase.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/SubscriptionBase.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Subscription and SubscriptionHold 
 * 
 * @author Jeff Schnitzer
 */
@MappedSuperclass
public class SubscriptionBase implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(SubscriptionBase.class);
	
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
	MailingList list;
	
	/**
	 * This overlaps with the relationship and exists solely so
	 * that we can put a Subscription in a Map on the Person.
	 * No getters or setters.
	 */
	@Column(name="listId", nullable=false, insertable=false, updatable=false)
	Long listId;
	
	/**
	 * A value of null means that mail should not be delivered. 
	 */
	@ManyToOne
	@JoinColumn(name="deliverToId", nullable=true)
	EmailAddress deliverTo;
	
	/** */
	@Column(nullable=false)
	Date dateCreated;
	
	/**
	 */
	public SubscriptionBase() {}
	
	/**
	 */
	public SubscriptionBase(Person person, MailingList list, EmailAddress deliverTo)
	{
		this.person = person;
		this.list = list;
		
		// This involves some validation
		this.setDeliverTo(deliverTo);
		
		this.dateCreated = new Date();
	}
	
	/** */
	public Person getPerson() { return this.person; }
	public void setPerson(Person value) { this.person = value; }
	
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

	/** */
	public Date getDateCreated() { return this.dateCreated; }
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", list=" + this.list + ", person=" + this.person + "}";
	}
}
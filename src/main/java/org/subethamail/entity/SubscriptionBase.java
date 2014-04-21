package org.subethamail.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.extern.java.Log;

/**
 * Base class for Subscription and SubscriptionHold 
 * 
 * @author Jeff Schnitzer
 */
@MappedSuperclass
@Log
public class SubscriptionBase implements Serializable
{
	private static final long serialVersionUID = 1L;

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
	    log.log(Level.FINE,"Setting deliverTo to {0}", value);
		
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
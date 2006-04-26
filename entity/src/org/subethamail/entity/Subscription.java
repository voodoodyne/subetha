/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({
	@NamedQuery(
		name="SubscriptionsByRoleId", 
		query="from Subscription s where s.role.id = :roleId",
		hints={
			// We want to writable and caching is pointless
		}
	),
	/*
	@NamedQuery(
			name="SearchSubscribers", 
			query="from Subscription s, MailingList l where s.listId = l.id and s.person.",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		)
	*/
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Subscription implements Serializable
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
	
	/** 
	 * The role of this subscription.
	 */
	@ManyToOne(optional=false)
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
		
		this.person = person;
		this.list = list;
		
		// This involves some validation
		this.setDeliverTo(deliverTo);
		this.setRole(role);
		
		// Notes should always start out empty
		this.note = "";
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

	/**
	 * FIXME ? This code is duplicated from AccountMgrBean.java
	 * 
	 * @return Gets a String[] of email addresses the Person is subscribed to.
	 */
	public String[] getEmailAddresses()
	{
		String[] addresses = new String[person.getEmailAddresses().size()];
		int i = 0;
		for (EmailAddress addy: person.getEmailAddresses().values())
		{
			addresses[i] = addy.getId();
			i++;
		}
		return addresses;
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
		if (log.isDebugEnabled())
			log.debug("Setting note of " + this + " to " + value);
		
		this.note = value;
	}
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", list=" + this.list + ", person=" + this.person + "}";
	}
}


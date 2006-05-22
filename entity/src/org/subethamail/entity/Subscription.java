/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
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
			// We want to write and caching is pointless
		}
	),
	@NamedQuery(
		name="CountSubscribersOnList", 
		query="select count(*) from Subscription s where s.listId = :listId",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="SubscribersOnList", 
		query="from Subscription sub where sub.list.id = :listId",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
			name="CountSubscribersOnListQuery", 
			query="select count(*) from Subscription sub " +
					"join sub.person.emailAddresses as email " +
					"where sub.list.id = :listId and " +
					"(sub.person.name like :name or email.id like :email)",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		),
	@NamedQuery(
		name="SubscribersOnListQuery", 
		query="select sub from Subscription sub " +
				"join sub.person.emailAddresses as email " +
				"where sub.list.id = :listId and " +
				"(sub.person.name like :name or email.id like :email)",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Subscription extends SubscriptionBase
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Subscription.class);
	
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
		super(person, list, deliverTo);
		
		// This involves some validation
		this.setRole(role);
		
		// Notes should always start out empty
		this.note = "";
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
}


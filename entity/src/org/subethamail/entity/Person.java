/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.Length;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.Validator;

/**
 * Entity of a human user of the system.
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
	@NamedQuery(
		name="SiteAdmin", 
		query="from Person p where p.siteAdmin = true order by p.name",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
			name="CountPerson", 
			query="select count(*) from Person",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Person implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Person.class);
	
	/** */
	static final Set<String> USER_ROLES = Collections.singleton("user");
	static final Set<String> SITE_ADMIN_ROLES;
	static
	{
		Set<String> roles = new HashSet<String>();
		roles.add("user");
		roles.add("siteAdmin");
		
		SITE_ADMIN_ROLES = Collections.unmodifiableSet(roles);
	}
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	@Column(name="passwd", nullable=false, length=Validator.MAX_PERSON_PASSWORD)
	@Length(min=3)
	String password;
	
	@Column(nullable=false, length=Validator.MAX_PERSON_NAME)
	String name;
	
	@Column(nullable=false)
	boolean siteAdmin;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="person")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	@MapKey(name="id")
	Map<String, EmailAddress> emailAddresses;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="person")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	@MapKey(name="listId")
	Map<Long, Subscription> subscriptions;
	
	/** not cached */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="person")
	@MapKey(name="listId")
	Map<Long, SubscriptionHold> heldSubscriptions;
	
	/**
	 */
	public Person() {}
	
	/**
	 */
	public Person(String password, String name)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new person");
		
		// These are validated normally.
		this.setPassword(password);
		this.setName(name);
		
		this.emailAddresses = new HashMap<String, EmailAddress>();
		this.subscriptions = new HashMap<Long, Subscription>();
		this.heldSubscriptions = new HashMap<Long, SubscriptionHold>();
	}
	
	/** */
	public Long getId()		{ return this.id; }
	
	/**
	 * TODO:  consider minimal two-way encryption so that pws are not easily readable in db
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * Note that the password is stored in cleartext so that
	 * we can email it back to its owner.
	 * 
	 * TODO:  consider minimal two-way encryption (even rot13) so that pws are not easily readable in db dumps
	 * 
	 * @param value is a plaintext password
	 */
	public void setPassword(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting password of " + this);
		
		this.password = value;
	}

	/**
	 * Checks to see if the password matches.
	 *
	 * @return true if the password does match.
	 */
	public boolean checkPassword(String plainText)
	{
		return this.password.equals(plainText);
	}
	
	/**
	 */
	public String getName() { return this.name; }
	
	/**
	 */
	public void setName(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting name of " + this + " to " + value);
		
		this.name = value;
	}

	/** */
	public boolean isSiteAdmin()
	{
		return this.siteAdmin;
	}

	/** */
	public void setSiteAdmin(boolean value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting admin flag of " + this + " to " + value);
		
		this.siteAdmin = value;
	}
	
	/** */
	public Map<String, EmailAddress> getEmailAddresses() { return this.emailAddresses; }
	
	/** */
	public void addEmailAddress(EmailAddress value)
	{
		this.emailAddresses.put(value.getId(), value);
	}
	
	/**
	 * Normalizes the email address first.  Ensures that the
	 * last email address cannot be removed.
	 */
	public EmailAddress removeEmailAddress(String email)
	{
		if (this.emailAddresses.size() <= 1)
			throw new IllegalStateException("Cannot remove last email address");
		
		email = Validator.normalizeEmail(email);
		
		// This odd construct is to work around hibernate bug HHH-2142
		EmailAddress addy = this.emailAddresses.get(email);
		this.emailAddresses.remove(email);
		return addy;
		
	}
	
	/**
	 * Gets the email address associated with that email, properly
	 * normalizing before checking.
	 * 
	 * @return null if that is not one of my email addresses.
	 */
	public EmailAddress getEmailAddress(String email)
	{
		email = Validator.normalizeEmail(email);
		return this.emailAddresses.get(email);
	}
	
	/**
	 * Convenience method
	 */
	public List<String> getEmailList()
	{
		List<String> addresses = new ArrayList<String>(this.emailAddresses.size());
		
		int i = 0;
		for (EmailAddress addy: this.emailAddresses.values())
		{
			addresses.add(addy.getId());
			i++;
		}
		
		// This wouldn't be necessary if @Sort worked on Maps
		Collections.sort(addresses);
		
		return addresses;
	}
	
	/** 
	 * @return all the subscriptions associated with this person
	 */
	public Map<Long, Subscription> getSubscriptions() { return this.subscriptions; }
	
	/**
	 * Adds a subscription to our internal map.
	 */
	public void addSubscription(Subscription value)
	{
		this.subscriptions.put(value.getList().getId(), value);
	}
	
	/**
	 * @return true if this person is subscribed to the list
	 */
	public boolean isSubscribed(MailingList list)
	{
		return this.subscriptions.containsKey(list.getId());
	}
	
	/**
	 * @return the subscription, or null if not subscribed to the list
	 */
	public Subscription getSubscription(Long listId)
	{
		return this.subscriptions.get(listId);
	}
	
	/**
	 * @return (not cached) the subscription holds for this user
	 */
	public Map<Long, SubscriptionHold> getHeldSubscriptions() { return this.heldSubscriptions; }
	
	/**
	 * Convenience method
	 */
	public void addHeldSubscription(SubscriptionHold hold)
	{
		this.heldSubscriptions.put(hold.getList().getId(), hold);
	}
	
	/** */
	public Role getRoleIn(MailingList list)
	{
		Subscription sub = this.subscriptions.get(list.getId());
		
		return (sub == null) ? list.getAnonymousRole() : sub.getRole();
	}
	
	/** */
	public Set<Permission> getPermissionsIn(MailingList list)
	{
		if (this.siteAdmin)
			return Permission.ALL;
		else
			return this.getRoleIn(list).getPermissions();
	}
	
	/** @return the j2ee security roles associated with this person */
	public Set<String> getRoles()
	{
		if (this.siteAdmin)
			return SITE_ADMIN_ROLES;
		else
			return USER_ROLES;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + ", emailAddresses=" + this.emailAddresses + "}";
	}

	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(Object arg0)
	{
		Person other = (Person)arg0;

		return this.name.compareTo(other.getName());
	}

}


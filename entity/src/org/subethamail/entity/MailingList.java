/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Email;
import org.subethamail.common.valid.Validator;

/**
 * Entity for a single mailing list
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
	@NamedQuery(
		name="MailingListByEmail", 
		query="from MailingList l where l.email = :email",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="MailingListByUrl", 
		query="from MailingList l where l.url = :url",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="AllMailingLists", 
		query="from MailingList l",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class MailingList implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(MailingList.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_LIST_EMAIL)
	@Email
	String email;
	
	@Column(nullable=false, length=Validator.MAX_LIST_NAME)
	String name;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_LIST_URL)
	String url;
	
	@Column(nullable=false, length=Validator.MAX_LIST_DESCRIPTION)
	String description;
	
	//
	// TODO:  set these two columns back to nullable=false when
	// this hibernate bug is fixed:
	// http://opensource.atlassian.com/projects/hibernate/browse/HHH-1654
	// Also, this affects the creation sequence for mailing lists
	//
	
	/** The default role for new subscribers */
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="defaultRoleId", nullable=true)
	Role defaultRole;
	
	/** The role to consider anonymous (not subscribed) people */
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="anonymousRoleId", nullable=true)
	Role anonymousRole;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Sort(type=SortType.NATURAL)
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	SortedSet<Subscription> subscriptions;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Set<EnabledFilter> enabledFilters;
	
	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Set<Role> roles;
	
	/**
	 */
	public MailingList() {}
	
	/**
	 */
	public MailingList(String email, String name, String url, String description)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new mailing list");
		
		// These are validated normally.
		this.setEmail(email);
		this.setName(name);
		this.setUrl(url);
		this.setDescription(description);
		
		// Make sure collections start empty
		this.subscriptions = new TreeSet<Subscription>();
		this.enabledFilters = new HashSet<EnabledFilter>();
		this.roles = new HashSet<Role>();
		
		// We have to start with one role, the owner role
		Role owner = new Role(this);
		this.roles.add(owner);
		
// TODO:  restore this code when hibernate bug fixed.  In the mean time,
// the creator MUST persist the MailingList *then* set these values.
// http://opensource.atlassian.com/projects/hibernate/browse/HHH-1654
//		this.defaultRole = owner;
//		this.anonymousRole = owner;
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/**
	 */
	public String getEmail() { return this.email; }
	
	/**
	 */
	public void setEmail(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting email of " + this + " to " + value);
		
		this.email = value;
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
	
	/**
	 */
	public String getUrl() { return this.url; }
	
	/**
	 */
	public void setUrl(String value)
	{
		if (value == null || value.length() > Validator.MAX_LIST_URL)
			throw new IllegalArgumentException("Invalid url");

		if (log.isDebugEnabled())
			log.debug("Setting url of " + this + " to " + value);
		
		this.url = value;
	}
	
	/**
	 */
	public String getDescription() { return this.description; }
	
	/**
	 */
	public void setDescription(String value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting description of " + this + " to " + value);
		
		this.description = value;
	}
	
	/** 
	 * @return all the subscriptions associated with this list
	 */
	public Set<Subscription> getSubscriptions() { return this.subscriptions; }
	
	/** 
	 * @return all plugins enabled on this list
	 */
	public Set<EnabledFilter> getEnabledFilters() { return this.enabledFilters; }

	/** 
	 * @return all roles available for this list
	 */
	public Set<Role> getRoles() { return this.roles; }
	
	/**
	 * @return true if role is valid for this list
	 */
	public boolean isValidRole(Role role)
	{
		return this.roles.contains(role);
	}
	
	/** */
	public Role getDefaultRole() { return this.defaultRole; }
	
	public void setDefaultRole(Role value)
	{
		if (!this.isValidRole(value))
			throw new IllegalArgumentException("Role belongs to some other list");
			
		this.defaultRole = value;
	}
	
	/** */
	public Role getAnonymousRole() { return this.anonymousRole; }

	public void setAnonymousRole(Role value)
	{
		if (!this.isValidRole(value))
			throw new IllegalArgumentException("Role belongs to some other list");
			
		this.anonymousRole = value;
	}
	
	/**
	 * Figures out which role is the owner and returns it
	 */
	public Role getOwnerRole()
	{
		for (Role check: this.roles)
			if (check.isOwner())
				return check;
		
		throw new IllegalStateException("Missing owner role");
	}
	
	/**
	 * Figures out the role for a person.  If pers is null,
	 * returns the anonymous role. 
	 */
	public Role getRoleFor(Person pers)
	{
		if (pers == null)
			return this.anonymousRole;
		else
			return pers.getRoleIn(this);
	}
	
	/**
	 * @return the context root of the SubEtha web application, determined
	 *  from the main URL.
	 */
	public String getUrlBase()
	{
		//TODO:  make this a constant somewhere
		
		int pos = this.url.indexOf("/se/list/");
		if (pos < 0)
			throw new IllegalStateException("Malformed list url");
		
		return this.url.substring(0, pos + "/se/".length()); 
	}
	
	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", address=" + this.email + "}";
	}

	/**
	 * Natural sort order is based on email address
	 */
	public int compareTo(Object arg0)
	{
		MailingList other = (MailingList)arg0;

		return this.email.compareTo(other.getEmail());
	}
}


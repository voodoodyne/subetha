/*
 * $Id: MailingList.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/MailingList.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;

import lombok.extern.java.Log;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.Email;
import org.subethamail.core.util.ContextAware;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;
import org.subethamail.entity.i.Validator;
import org.subethamail.smtp.util.EmailUtils;

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
		query="from MailingList l order by l.name",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
			name="SearchMailingLists",
			query="from MailingList l where (l.name like :name) or " +
											"(l.email like :email) or" +
											"(l.url like :url) or" +
											"(l.description like :description) order by l.name",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		),
	@NamedQuery(
			name="CountMailingLists",
			query="select count(*) from MailingList l",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		),
	@NamedQuery(
			name="CountMailingListsQuery",
			query="select count(*) from MailingList l where (l.name like :name) or " +
											"(l.email like :email) or" +
											"(l.url like :url) or" +
											"(l.description like :description)",
			hints={
				@QueryHint(name="org.hibernate.readOnly", value="true"),
				@QueryHint(name="org.hibernate.cacheable", value="true")
			}
		)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@Log
public class MailingList implements Serializable, Comparable<MailingList>
{
	private static final long serialVersionUID = 1L;

	/**
	 * TreeSet requires a weird comparator because it uses the comparator
	 * instead of equals().  When we return 0, it means the objects must
	 * really be equal.
	 */
	public static class SubscriptionComparator implements Comparator<Subscription>
	{
		public int compare(Subscription s1, Subscription s2)
		{
			if (s1 == null || s2 == null)
				return 0;

			int result = s1.getPerson().compareTo(s2.getPerson());

			if (result == 0)
				return s1.getPerson().getId().compareTo(s2.getPerson().getId());
			else
				return result;
		}
	};

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

	/** Hold subs for moderator approval */
	@Column(nullable=false)
	boolean subscriptionHeld;

	@Column(nullable=false, length=Validator.MAX_LIST_WELCOME_MESSAGE)
	String welcomeMessage;

	//
	// TODO:  set these two columns back to nullable=false when
	// this hibernate bug is fixed:
	// http://opensource.atlassian.com/projects/hibernate/browse/HHH-1654
	// Also, this affects the creation sequence for mailing lists
	//

	/** The default role for new subscribers */
	@OneToOne
	@JoinColumn(name="defaultRoleId", nullable=true)
	Role defaultRole;

	/** The role to consider anonymous (not subscribed) people */
	@OneToOne
	@JoinColumn(name="anonymousRoleId", nullable=true)
	Role anonymousRole;

	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Sort(type=SortType.COMPARATOR, comparator=SubscriptionComparator.class)
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	SortedSet<Subscription> subscriptions;

	/** not cached */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@OrderBy(value="dateCreated")
	Set<SubscriptionHold> subscriptionHolds;

	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	@MapKey(name="className")
	Map<String, EnabledFilter> enabledFilters;

	/** */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	@OrderBy(value="name")
	Set<Role> roles;

	/** The only reason this is here is to provide cascading delete */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="list")
	Set<Mail> mails;

	/**
	 */
	public MailingList() {}

	/**
	 */
	public MailingList(String email, String name, String url, String description)
	{
	    log.log(Level.FINE,"Creating new mailing list");

		// These are validated normally.
		this.setEmail(email);
		this.setName(name);
		this.setUrl(url);
		this.setDescription(description);

		// Make sure collections start empty
		this.subscriptions = new TreeSet<Subscription>(new SubscriptionComparator());
		this.enabledFilters = new HashMap<String, EnabledFilter>();
		this.roles = new HashSet<Role>();
		this.subscriptionHolds = new HashSet<SubscriptionHold>();

		// We have to start with one role, the owner role
		Role owner = new Role(this);
		this.roles.add(owner);

		this.welcomeMessage = "";

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
	    log.log(Level.FINE,"Setting email of {0} to {1}", new Object[]{this, value});

		value = EmailUtils.normalizeEmail(value);
		
		this.email = value;
	}

	/**
	 */
	public String getName() { return this.name; }

	/**
	 */
	public void setName(String value)
	{
	    log.log(Level.FINE,"Setting name of {0} to {1}", new Object[]{this,value});

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
		
		try { new URL(value); }
		catch (MalformedURLException e) { throw new IllegalArgumentException("Invalid url"); }
		
		log.log(Level.FINE,"Setting url of {0} to {1}", new Object[]{this, value});

		this.url = value;
	}

	/**
	 */
	public String getDescription() { return this.description; }

	/**
	 */
	public void setDescription(String value)
	{
	    log.log(Level.FINE,"Setting description of {0} to {1}", new Object[]{this, value});

		this.description = value;
	}

	public String getWelcomeMessage()
	{
		return this.welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage)
	{
	    log.log(Level.FINE,"Setting welcomeMessage of {0} to {1}", new Object[]{this,welcomeMessage});

		this.welcomeMessage = welcomeMessage;
	}

	/**
	 */
	public boolean isSubscriptionHeld() { return this.subscriptionHeld; }

	public void setSubscriptionHeld(boolean value)
	{
		this.subscriptionHeld = value;
	}

	/**
	 * @return all the subscriptions associated with this list
	 */
	public Set<Subscription> getSubscriptions() { return this.subscriptions; }

	/**
	 * @return all the held subscriptions
	 */
	public Set<SubscriptionHold> getSubscriptionHolds() { return this.subscriptionHolds; }

	/**
	 * @return all plugins enabled on this list
	 */
	public Map<String, EnabledFilter> getEnabledFilters() { return this.enabledFilters; }

	/**
	 * Convenience method.
	 */
	public void addEnabledFilter(EnabledFilter filt)
	{
		this.enabledFilters.put(filt.getClassName(), filt);
	}

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
	 * Figures out the permissions for a person.  Very
	 * similar to getRoleFor() but takes into account
	 * site adminstrators which always have permission.
	 */
	public Set<Permission> getPermissionsFor(Person pers)
	{
		if (pers == null)
			return this.anonymousRole.getPermissions();
		else
			return pers.getPermissionsIn(this);
	}

	/**
	 * @param pers can be null to indicate anonymous person.  Site admins have all permissions.
	 * @throws PermissionException if person doesn't have the permission
	 */
	public void checkPermission(Person pers, Permission check) throws PermissionException
	{
		if (! this.getPermissionsFor(pers).contains(check))
			throw new PermissionException(check);
	}

	/**
	 * Uses the URL for the list and the context path to figure out the
	 * appropriate path to the SubEtha instance.  Eg:
	 * 
	 * http://my.list/ctx/somelist -> http://my.list/ctx/
	 * 
	 * @return the context root of the SubEtha web application, determined
	 *  from the main URL.  Includes trailing /.
	 */
	public String getUrlBase()
	{
		String contextPath = ContextAware.getContextPath();
		
		try
		{
			URL u = new URL(this.url);
			
			return u.getProtocol() 
				+ "://" 
				+ u.getHost() 
				+ ((u.getPort() < 0) ? "" : (":"+u.getPort()))
				+ contextPath
				+ "/";
		}
		catch (MalformedURLException e)
		{
			// Should be impossible
			throw new IllegalStateException("Stored an illegal url " + this.url);
		}
	}

	/**
	 * @return the owner email address for this list
	 */
	public String getOwnerEmail()
	{
		int atIndex = this.email.indexOf('@');

		String box = this.email.substring(0, atIndex);
		String remainder = this.email.substring(atIndex);

		return box + "-owner" + remainder;
	}

	/** */
	@Override
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", address=" + this.email + "}";
	}

	/**
	 * Natural sort order is based on email address
	 */
	public int compareTo(MailingList other)
	{
		return this.email.compareTo(other.getEmail());
	}
}
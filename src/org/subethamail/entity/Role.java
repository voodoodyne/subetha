/*
 * $Id: Role.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/Role.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.Validator;

/**
 * A mailing list can have any number of roles which define a set of
 * permissions.  Subscribers to a list have one role.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Role implements Serializable, Comparable<Role>
{
	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(Role.class);

	/** */
	@Transient public static final String OWNER_NAME = "Owner";

	/** */
	@Id
	@GeneratedValue
	Long id;

	/** */
	@Column(nullable=false, length=Validator.MAX_ROLE_NAME)
	@Length(min=1)
	String name;

	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList list;

	/** */
	@Column(nullable=false)
	boolean owner;

	/** */
	@ElementCollection
	@JoinTable(name="RolePermission", joinColumns={@JoinColumn(name="roleId")})
	@Enumerated(EnumType.STRING)
	@Column(name="perm", nullable=false)
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Set<Permission> permissions;

	/**
	 * The presence of this relationship makes Hibernate smart enough to
	 * delete a MailingList.  Without it, Roles are deleted before Subscriptions
	 * and a not null constraint on Subscription.role is violated.  We don't
	 * actually use this relationship.
	 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="role")
	Set<Subscription> subscriptions;

	/**
	 */
	public Role() {}

	/**
	 * Creates a new owner role.  There should be at most one of these
	 * in any given list.
	 */
	public Role(MailingList list)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new owner Role");

		this.list = list;
		this.setName(OWNER_NAME);
		this.owner = true;
	}

	/**
	 */
	public Role(MailingList list, String name, Set<Permission> permissions)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new Role");

		this.list = list;
		this.setName(name);

		this.permissions = permissions;
	}

	/** */
	public Long getId()		{ return this.id; }

	/** */
	public MailingList getList() { return this.list; }

	/** */
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
	public boolean isOwner() { return this.owner; }

	/**
	 * If this is the owner role, return an unmodifiable set of all permissions.
	 * Otherwise, return the normal set of permissions.
	 */
	public Set<Permission> getPermissions()
	{
		if (this.owner)
			return Permission.ALL;
		else
			return this.permissions;
	}

	/** */
	@Override
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(Role other)
	{
		return this.name.compareTo(other.getName());
	}
}
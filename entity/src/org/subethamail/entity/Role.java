/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.Length;
import org.subethamail.common.Permission;
import org.subethamail.common.valid.Validator;

/**
 * A mailing list can have any number of roles which define a set of
 * permissions.  Subscribers to a list have one role.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class Role implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Role.class);
	
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
	@CollectionOfElements
	@JoinTable(name="RolePermission", joinColumns={@JoinColumn(name="roleId")})
	@Enumerated(EnumType.STRING)
	@Column(name="perm", nullable=false)
	@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
	Set<Permission> permissions;
	
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
	public Role(MailingList list, String name)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new Role");
		
		this.list = list;
		this.setName(name);
		
		this.permissions = new HashSet<Permission>();
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
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/**
	 * Natural sort order is based on name
	 */
	public int compareTo(Object arg0)
	{
		Role other = (Role)arg0;

		return this.name.compareTo(other.getName());
	}
}


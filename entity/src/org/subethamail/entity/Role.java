/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * A mailing list can have any number of roles which define a set of
 * permissions.  Subscribers to a list have one role.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Role.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** */
	@Column(nullable=false, length=Validator.MAX_ROLE_NAME)
	String name;
	
	/** */
	@ManyToOne
	@JoinColumn(name="listId", nullable=false)
	MailingList mailingList;
	
	/** */
	@Column(nullable=false)
	boolean canEditRoles;
	
	@Column(nullable=false)
	boolean canEditPlugins;
	
	@Column(nullable=false)
	boolean canApproveMessages;
	
	@Column(nullable=false)
	boolean canApproveSubscriptions;
	
	@Column(nullable=false)
	boolean canPost;
	
	@Column(nullable=false)
	boolean canViewSubscribers;
	
	@Column(nullable=false)
	boolean canReadArchives;
	
	@Column(nullable=false)
	boolean canReadNotes;
	
	@Column(nullable=false)
	boolean canEditNotes;
	
	/**
	 */
	public Role() {}
	
	/**
	 */
	public Role(MailingList list, String name)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new Role");
		
		this.mailingList = list;
		this.setName(name);
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/** */
	public MailingList getMailingList() { return this.mailingList; }
	
	/** */
	public String getName() { return this.name; }
	
	/**
	 */
	public void setName(String value)
	{
		if (value == null || value.length() == 0 || value.length() > Validator.MAX_ROLE_NAME)
			throw new IllegalArgumentException("Invalid name");

		if (log.isDebugEnabled())
			log.debug("Setting name of " + this + " to " + value);
		
		this.name = value;
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


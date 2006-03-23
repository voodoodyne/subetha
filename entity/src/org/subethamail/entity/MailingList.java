/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
 * Entity for a single mailing list
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
	@NamedQuery(
		name="MailingListByAddress", 
		query="from MailingList l where l.address = :address",
		hints={
			@QueryHint(name="org.hibernate.readOnly", value="true"),
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MailingList implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(MailingList.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	/** TODO:  this should be stored as separate components */
	@Column(nullable=false, length=Validator.MAX_LIST_ADDRESS)
	String address;
	
	/** */
	@Column(nullable=false)
	String url;
	
	@Column(nullable=false)
	String name;
	
	@Column(nullable=false)
	String description;
	
	@Column(nullable=false)
	String subjectPrefix;
	
	/** */
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(
		name="listAdministrators",
		joinColumns={@JoinColumn(name="listId")},
		inverseJoinColumns={@JoinColumn(name="personId")}
	)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	Set<Person> administrators;
	
	/** */
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(
		name="listModerators",
		joinColumns={@JoinColumn(name="listId")},
		inverseJoinColumns={@JoinColumn(name="personId")}
	)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	Set<Person> moderators;
	
	/** */
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(
		name="listMembers",
		joinColumns={@JoinColumn(name="listId")},
		inverseJoinColumns={@JoinColumn(name="personId")}
	)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	Set<Person> members;
	
	/**
	 */
	public MailingList() {}
	
	/**
	 */
	public MailingList(String emailAddress)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new mailing list");
		
		// These are validated normally.
		this.setAddress(emailAddress);
	}
	
	/** */
	public Long getId()		{ return this.id; }

	/**
	 */
	public String getAddress() { return this.address; }
	
	/**
	 */
	public void setAddress(String value)
	{
		if (value == null || value.length() > Validator.MAX_LIST_ADDRESS)
			throw new IllegalArgumentException("Invalid list address");

		if (log.isDebugEnabled())
			log.debug("Setting address of " + this + " to " + value);
		
		this.address = value;
	}

	/** */
	public String toString()
	{
		return this.getClass() + " {id=" + this.id + ", address=" + this.address + "}";
	}

	/**
	 * Natural sort order is based on email address
	 */
	public int compareTo(Object arg0)
	{
		MailingList other = (MailingList)arg0;

		return this.address.compareTo(other.getAddress());
	}
}


/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

package org.subethamail.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.Email;
import org.subethamail.common.valid.Validator;

/**
 * Entity of an email address.  People can have many email
 * addresses, any of which can be used to send mail.  Note that
 * all addresses are stored in their normalized form.
 * 
 * @see Validator#normalizeEmail(String)
 * 
 * Note that this entity uses a natural key - the email address
 * itself.  This enables efficient 2nd-level cache lookups.
 * One consequence is that you never change email addresses;
 * you add new ones and delete old ones. 
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class EmailAddress implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(EmailAddress.class);
	
	/** */
	@Id
	@Column(length=Validator.MAX_EMAIL_ADDRESS)
	@Email
	String id;
	
	@ManyToOne
	@JoinColumn(name="personId", nullable=false)
	Person person;
	
	/**
	 */
	public EmailAddress() {}
	
	/**
	 */
	public EmailAddress(Person person, String email)
	{
		if (log.isDebugEnabled())
			log.debug("Creating new EmailAddress");
		
		this.setId(email);
		this.setPerson(person);
	}
	
	/** */
	public String getId() { return this.id; }
	
	/**
	 * Always normalizes the email address.  Note that this
	 * is not public - only settible when creating the object.
	 */
	private void setId(String value)
	{
		value = Validator.normalizeEmail(value);

		if (log.isDebugEnabled())
			log.debug("Setting address to " + value);
		
		this.id = value;
	}
	
	/** */
	public Person getPerson() { return this.person; }
	
	public void setPerson(Person value)
	{
		this.person = value;
	}

	/** */
	public String toString()
	{
		return this.getClass() + "{id=" + this.id + "}";
	}

	/**
	 * Natural sort order is based on email text
	 */
	public int compareTo(Object arg0)
	{
		EmailAddress other = (EmailAddress)arg0;

		return this.id.compareTo(other.getId());
	}
}


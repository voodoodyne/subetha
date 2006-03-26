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
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.subethamail.common.valid.Validator;

/**
 * Entity of a human user of the system.
 * 
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements Serializable, Comparable
{
	/** */
	@Transient private static Log log = LogFactory.getLog(Person.class);
	
	/** */
	@Id
	@GeneratedValue
	Long id;
	
	@Column(name="passwd", nullable=false, length=Validator.MAX_PERSON_PASSWORD)
	String password;
	
	@Column(nullable=false, length=Validator.MAX_PERSON_NAME)
	String name;
	
	@Column(nullable=false)
	boolean admin;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="owner")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	Set<EmailAddress> emailAddresses;
	
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
	}
	
	/** */
	public Long getId()		{ return this.id; }

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
		if (value == null || value.length() < 3 || value.length() > Validator.MAX_PERSON_PASSWORD)
			throw new IllegalArgumentException("Invalid password");

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
		if (value == null || value.length() > Validator.MAX_PERSON_NAME)
			throw new IllegalArgumentException("Invalid name");

		if (log.isDebugEnabled())
			log.debug("Setting name of " + this + " to " + value);
		
		this.name = value;
	}

	/** */
	public boolean isAdmin()
	{
		return this.admin;
	}

	/** */
	public void setAdmin(boolean value)
	{
		if (log.isDebugEnabled())
			log.debug("Setting admin flag of " + this + " to " + value);
		
		this.admin = value;
	}
	
	/** */
	public Set<EmailAddress> getEmailAddresses() { return this.emailAddresses; }
	
	/** */
	public void setEmailAddresses(Set<EmailAddress> value)
	{
		this.emailAddresses = value;
	}
	
	/**
	 * @return true if this person is subscribed to the list
	 */
	public boolean isSubscribed(MailingList list)
	{
		// TODO
		return true;
	}
	
	/** */
	public String toString()
	{
		return "Person {id=" + this.id + ", emailAddresses=" + this.emailAddresses + "}";
	}

	/**
	 * Natural sort order is based on id
	 */
	public int compareTo(Object arg0)
	{
		Person other = (Person)arg0;

		return this.id.compareTo(other.getId());
	}
}


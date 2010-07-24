/*
 * $Id: EmailAddress.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/EmailAddress.java $
 */

package org.subethamail.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Validator;

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
 * Email addresses track their bounce count so we know when
 * to disable them.
 *
 * @author Jeff Schnitzer
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class EmailAddress implements Serializable, Comparable<EmailAddress>
{
	private static final long serialVersionUID = 1L;

	/** */
	@Transient private final static Logger log = LoggerFactory.getLogger(EmailAddress.class);

	/**
	 * We only increment or decrement once for this period, in millis.
	 * One day.
	 */
	@Transient public static final long BOUNCE_IGNORE_PERIOD = 1000 * 60 * 60 * 24;

	/** */
	@Id
	@Column(length=Validator.MAX_EMAIL_ADDRESS)
	String id;

	/** */
	@ManyToOne
	@JoinColumn(name="personId", nullable=false)
	Person person;

	/**
	 * Not actually the number of bounces, but this number does
	 * go up when bounces happen.
	 */
	@Column(nullable=false)
	int bounces;

	@Column(nullable=true)
	Date lastBounceIncrement;

	@Column(nullable=true)
	Date lastBounceDecrement;

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

	/**
	 * Not actually the number of bounces, but this number does
	 * go up when bounces happen. It decays if bounces don't.
	 */
	public int getBounces() { return this.bounces; }

	/**
	 * When a bounce occurs, it increments the count by 2.  Only
	 * do this once per day.
	 */
	public void bounceIncrement()
	{
		if (this.lastBounceIncrement == null
				|| System.currentTimeMillis() >
					(this.lastBounceIncrement.getTime() + BOUNCE_IGNORE_PERIOD))
		{
			this.bounces += 2;
			this.lastBounceIncrement = new Date();
		}
	}

	/**
	 * When mail goes out, the bounce count should decay slightly.
	 * Only do this once per day.
	 */
	public void bounceDecay()
	{
		if (this.bounces > 0)
		{
			if (this.lastBounceDecrement == null
					|| System.currentTimeMillis() >
						(this.lastBounceDecrement.getTime() + BOUNCE_IGNORE_PERIOD))
			{
				this.bounces--;
				this.lastBounceDecrement = new Date();
			}
		}
	}

	/** */
	@Override
	public String toString()
	{
		return this.getClass().getName() + "{id=" + this.id + "}";
	}

	/**
	 * Natural sort order is based on email text
	 */
	public int compareTo(EmailAddress other)
	{
		return this.id.compareTo(other.getId());
	}
}
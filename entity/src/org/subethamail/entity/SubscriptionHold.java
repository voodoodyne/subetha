/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A request to join a mailing list. 
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class SubscriptionHold extends SubscriptionBase
{
	/** */
	@SuppressWarnings("unused")
	@Transient private static Log log = LogFactory.getLog(SubscriptionHold.class);
	
	/**
	 */
	public SubscriptionHold() {}
	
	/**
	 */
	public SubscriptionHold(Person person, MailingList list, EmailAddress deliverTo)
	{
		super(person, list, deliverTo);
	}
}


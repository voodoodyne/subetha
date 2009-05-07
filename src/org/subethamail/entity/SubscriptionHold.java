/*
 * $Id: SubscriptionHold.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/entity/SubscriptionHold.java $
 */

package org.subethamail.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A request to join a mailing list. 
 * 
 * @author Jeff Schnitzer
 */
@NamedQueries({
	@NamedQuery(
		name="HeldSubscriptionCount", 
		query="select count(*) from SubscriptionHold h where h.list.id = :listId",
		hints={
			@QueryHint(name="org.hibernate.cacheable", value="true")
		}
	),
	@NamedQuery(
		name="HeldSubscriptionsOlderThan", 
		query="select h from SubscriptionHold h where h.dateCreated < :cutoff",
		hints={
		}
	)
})
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@SuppressWarnings("serial")
public class SubscriptionHold extends SubscriptionBase
{
	/** */
	@SuppressWarnings("unused")
	@Transient private final static Logger log = LoggerFactory.getLogger(SubscriptionHold.class);
	
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
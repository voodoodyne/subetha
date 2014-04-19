package org.subethamail.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
public class SubscriptionHold extends SubscriptionBase
{
	private static final long serialVersionUID = 1L;

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
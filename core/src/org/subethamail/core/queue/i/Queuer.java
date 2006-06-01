/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue.i;

import java.util.List;

import javax.ejb.Local;

/**
 * Interface for queueing mail for delivery.  There are two queues,
 * the inbound queue and the outbound queue.  The inbound queue contains
 * mailIds of mail that needs to be delivered to all recipients.  The
 * outbound queue contains mailId,personId pairs of mail that needs
 * to be delivered to specific recipients.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Queuer
{
	/** */
	public static final String JNDI_NAME = "subetha/Queuer/local";

	/**
	 * Queues a mail for delivery to all receipients that want this
	 * piece of mail (ie, subscribed to the list and have delivery enabled).
	 * This puts the mailId on the inbound queue and returns.  
	 */
	public void queueForDelivery(Long mailId);
	
	/**
	 * Queues a mail for delivery to a specific person.  This puts
	 * the mailId,personId pair on the outbound queue and returns.
	 * 
	 * TODO:  consider - should delivery options be considered here?
	 * Probably not, just send the mail, since we wouldn't have gotten
	 * here if the user didn't want the mail.
	 */
	public void queueForDelivery(Long mailId, Long personId);
	
	/**
	 * Queues a mail for delivery to the specified people.  This puts
	 * the collection of mailId,personId pairs on the outbound queue
	 * and returns.
	 */
	public void queueForDelivery(Long mailId, List<Long> personIds);
}


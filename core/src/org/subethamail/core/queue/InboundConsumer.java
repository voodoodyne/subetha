/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Consumer;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;

/**
 * Consumer of the inbound queue.  This is a JBoss message-driven POJO.
 * 
 * @author Jeff Schnitzer
 */
@Consumer(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/subetha/inbound"),
		@ActivationConfigProperty(propertyName="minPoolSize", propertyValue="1"),
		@ActivationConfigProperty(propertyName="maxPoolSize", propertyValue="2")
	}
)
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class InboundConsumer extends EntityManipulatorBean implements Inbound
{
	/** */
	private static Log log = LogFactory.getLog(InboundConsumer.class);
	
	/** Number of messages to batch in each outbound queue request */
	static final int BATCH_SIZE = 10;
	
	/** */
	@EJB Queuer queuer;
	
	/**
	 * @see Inbound#deliver(Long)
	 */
	public void deliver(Long mailId)
	{
		if (log.isDebugEnabled())
			log.debug("Distributing mailId " + mailId);
		
		Mail mail;
		try
		{
			mail = this.em.get(Mail.class, mailId);
		}
		catch (NotFoundException ex)
		{
			// Possible if the message was deleted during the queue time.
			// Not a problem, just log an error and return, accepting the
			// queue message.
			
			if (log.isWarnEnabled())
				log.warn("Wanted to distribute nonexistant mailId " + mailId);
			
			return;
		}

// Upon further reflection, this is actually quite dangerous.  Since
// the message will be retried, all potential 9 prior recipients will
// get the message redelivered on each retry.  Ouch.  We're stuck with
// one-at-a-time processing; performance will have to come from optimizing
// JBossMQ or switching to JBossMessaging.
//
//		// Lookup all recipients and queue the mailId, recipientId pairs
//		// Rather than queue them one at a time, batch them in groups of 10.
//		// This right here is the most expensive operation in the delivery
//		// chain (at least with JBossMQ), so batching dramatically reduces
//		// the number of operations.  The only downside is that at worst,
//		// 9 people could receive duplicate messages.
//		List<Long> batch = new ArrayList<Long>(BATCH_SIZE);
//		
//		for (Subscription sub: mail.getList().getSubscriptions())
//		{
//			if (sub.getDeliverTo() != null)
//				batch.add(sub.getPerson().getId());
//			
//			if (batch.size() == BATCH_SIZE)
//			{
//				this.queuer.queueForDelivery(mailId, batch);
//				batch.clear();
//			}
//		}
//		
//		// Anything left in the batch must be delivered too
//		if (!batch.isEmpty())
//			this.queuer.queueForDelivery(mailId, batch);
		
		// The original one-at-a-time code
		for (Subscription sub: mail.getList().getSubscriptions())
		{
			if (sub.getDeliverTo() != null)
				this.queuer.queueForDelivery(mailId, sub.getPerson().getId());
		}
	}
}


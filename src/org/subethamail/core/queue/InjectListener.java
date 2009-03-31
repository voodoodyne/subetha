/*
 * $Id: $
 * $URL:  $
 */


package org.subethamail.core.queue;

import java.util.concurrent.BlockingQueue;

import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.util.DeliveryQueue;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;

@MessageDriven
public class InjectListener extends EntityManipulatorBean implements MessageListener{
	/** */
	private static Log log = LogFactory.getLog(InjectListener.class);

	/** Number of messages to batch in each outbound queue request */
	static final int BATCH_SIZE = 10;

	@DeliveryQueue
	BlockingQueue<UserMailDeliveryData> q;
	
	public void onMessage(Message qMsg) {
		try {
			Long id = (Long)((ObjectMessage) qMsg).getObject();
			this.deliver(id);
		} catch (JMSException e) {
			log.error("Error getting object outa message (from queue)",e);
		}
	}
	

	/**
	 * @see Inbound#deliver(Long)
	 */
	private void deliver(Long mailId)
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

		// The original one-at-a-time code
		for (Subscription sub: mail.getList().getSubscriptions())
		{
			if (sub.getDeliverTo() != null){
				try {
					q.put(new UserMailDeliveryData(mailId, sub.getPerson().getId()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error("Error queuing delivery message",e);
				}
				//this.queuer.queueForDelivery(mailId, sub.getPerson().getId());
			}
		}
	}
	
}

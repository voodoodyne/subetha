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
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;

/**
 * Queue which takes the individual, stored messages and turns them into
 * a series of delivery queue messages.  This could take a while; any
 * individual message may get turned into thousands of outboud queue
 * messages.
 */
@MessageDriven
public class InjectListener extends EntityManipulatorBean implements MessageListener{
	/** */
	private static Log log = LogFactory.getLog(InjectListener.class);

	/** */
	//TODO: Figure out why the injector is puking on this.
	//@DeliveryQueue 
//	@Name("delivery")
	BlockingQueue<MailDelivery> outboundQueue ;//= new ArrayBlockingQueue<MailDelivery>(3);

	/** */
	public void onMessage(Message qMsg)
	{
		try
		{
			Long id = (Long) ((ObjectMessage)qMsg).getObject();
			this.deliver(id);
		}
		catch (JMSException e)
		{
			log.error("Error getting object outa message (from queue)", e);
		}
	}
	
	/**
	 * Looks up who gets that message and creates new queue entries.
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

		// Now make sure each subscriber gets a copy
		for (Subscription sub: mail.getList().getSubscriptions())
		{
			if (sub.getDeliverTo() != null)
			{
				try
				{
					this.outboundQueue.put(new MailDelivery(mailId, sub.getPerson().getId()));
				}
				catch (InterruptedException e)
				{
					log.error("Error queuing delivery message",e);
				}
			}
		}
	}
	
}

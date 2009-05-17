/*
 * $Id: $
 * $URL:  $
 */


package org.subethamail.core.queue;

import java.util.concurrent.BlockingQueue;

import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;

import com.caucho.config.Name;

/**
 * Queue which takes the individual, stored messages and turns them into
 * a series of delivery queue messages.  This could take a while; any
 * individual message may get turned into thousands of outboud queue
 * messages.
 */
@MessageDriven
public class InjectListener implements MessageListener{
	/** */
	private final static Logger log = LoggerFactory.getLogger(InjectListener.class);

	/** */
	@SuppressWarnings("unchecked")
//	@DeliveryQueue 
	@Name("delivery")
	BlockingQueue outboundQueue;

	@SuppressWarnings("unchecked")
//	@InjectQueue 
	@Name("inject")
	BlockingQueue myQueue;

	/** */
	@SubEtha
	protected SubEthaEntityManager em;

	/** */
	@SuppressWarnings("unchecked")
	public void onMessage(Message qMsg)
	{
		boolean failed = true; 
		InjectedQueueItem item = null;
		try
		{
			item = (InjectedQueueItem) ((ObjectMessage)qMsg).getObject();
			try 
			{
				this.deliver(item.getMailId());
				failed = false;
			}
			catch (Exception e) { /*Eat it. The finally block will retry.*/ }
		}
		catch (JMSException e)
		{
			if(log.isErrorEnabled())
				log.error("Error getting object outa message (from queue)", e);
		}
		finally
		{
			if(failed) try
			{
				myQueue.put(item);
			}
			catch (InterruptedException e)
			{
				if(log.isErrorEnabled())
					log.error("Error requeing", e);				
			}
		}
	}
	
	/**
	 * Looks up who gets that message and creates new queue entries.
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
				Long personId = sub.getPerson().getId();
				try
				{
					this.outboundQueue.put(new DeliveryQueueItem(mailId, personId));
				}
				catch (InterruptedException e)
				{
					log.error("Error queuing delivery messages for mail.id=" + mailId + " person.id=" + personId, e);
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}

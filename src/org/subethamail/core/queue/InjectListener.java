/*
 * $Id: $
 * $URL:  $
 */


package org.subethamail.core.queue;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import lombok.extern.java.Log;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;

/**
 * Queue which takes the individual, stored messages and turns them into
 * a series of delivery queue messages.  This could take a while; any
 * individual message may get turned into thousands of outboud queue
 * messages.
 */
//@MessageDriven	// declared in resin-web.xml
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Log
public class InjectListener implements MessageListener
{
	/** Unfortunately the generics trip up Resin's CDI impl */
	//@Inject @DeliveryQueue BlockingQueue<DeliveryQueueItem> outboundQueue;
	@SuppressWarnings("rawtypes")
	@Inject @DeliveryQueue BlockingQueue outboundQueue;

	/** */
	@Inject @SubEtha SubEthaEntityManager em;

	/** */
	public void onMessage(Message qMsg)
	{
		InjectedQueueItem item = null;
		try
		{
			item = (InjectedQueueItem) ((ObjectMessage)qMsg).getObject();
			this.deliver(item.getMailId());
		}
		catch (JMSException e)
		{
			log.log(Level.SEVERE,"Error getting object outa message (from queue)", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Looks up who gets that message and creates new queue entries.
	 */
	@SuppressWarnings("unchecked")
	private void deliver(Long mailId)
	{
	    log.log(Level.FINE,"Distributing mailId {0}", mailId);

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

		    log.log(Level.WARNING,"Wanted to distribute nonexistant mailId {0}", mailId);

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
				    LogRecord logRecord=new LogRecord(Level.SEVERE,"Error queuing delivery messages for mail.id={0} person.id={1}");
				    logRecord.setParameters(new Object[]{mailId, personId});
				    logRecord.setThrown(e);
                    log.log(logRecord);
					throw new RuntimeException(e);
				}
			}
		}
	}
}
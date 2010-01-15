/*
 * $Id: $
 * $URL:  $
 */

package org.subethamail.core.queue;

import java.util.concurrent.BlockingQueue;

import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.deliv.i.Deliverator;


/**
 * Processes delivery queue messages by creating an actual STMP message
 * using JavaMail, relaying it through the {@link Deliverator}.
 */
@MessageDriven
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryListener implements MessageListener
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(DeliveryListener.class);

	/** */
	@Inject Deliverator deliverator;

	@SuppressWarnings("unchecked")
	@DeliveryQueue 
	@Inject
//	@Named("delivery")
	BlockingQueue myQueue;

	/**
	 */
	public void onMessage(Message qMsg)
	{
		try
		{
			DeliveryQueueItem item = (DeliveryQueueItem)((ObjectMessage) qMsg).getObject();

			Long mailId = item.getMailId();
			Long personId = item.getPersonId();

			if (log.isDebugEnabled())
				log.debug("Delivering mailId:" + mailId + " to personId:" + personId);
			
			try
			{
				this.deliverator.deliver(mailId, personId);
			}
			catch (NotFoundException ex)
			{
				// Just log a warning and accept the JMS message
				// It possible the message/subscription has been deleted since we queued the orig request.
				if (log.isWarnEnabled())
					log.warn("Unknown mailId(" + mailId + ") or personId(" + personId + ")", ex);
			}
		}
		catch (JMSException ex)
		{
			if (log.isErrorEnabled())
				log.error("Error getting data out of message.", ex);
			
			throw new RuntimeException(ex);
		}
	}
}

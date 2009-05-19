/*
 * $Id: $
 * $URL:  $
 */

package org.subethamail.core.queue;

import java.util.concurrent.BlockingQueue;

import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Current;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.deliv.i.Deliverator;

import com.caucho.config.Name;

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
	@Current Deliverator deliverator;

	@SuppressWarnings("unchecked")
//	@DeliveryQueue 
	@Name("delivery")
	BlockingQueue myQueue;

	/**
	 */
	public void onMessage(Message qMsg)
	{
		DeliveryQueueItem item = null;
		Long mailId , personId;
		try
		{
			item = (DeliveryQueueItem)((ObjectMessage) qMsg).getObject();

			mailId = item.getMailId();
			personId = item.getPersonId();

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
			catch (Exception e)
			{
				if (log.isErrorEnabled())
					log.error("Error processing message!", e);
				throw new RuntimeException(e);
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

/*
 * $Id: $
 * $URL:  $
 */


package org.subethamail.core.queue;

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

/**
 * Processes delivery queue messages by creating an actual STMP message
 * using JavaMail, relaying it through the deliverator.
 */
@MessageDriven
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryListener implements MessageListener
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(DeliveryListener.class);

	/** */
	@Current Deliverator deliverator;

	/**
	 */
	public void onMessage(Message qMsg)
	{
		DeliveryQueueItem umdd;
		Long mailId , personId;
		try
		{
			umdd = (DeliveryQueueItem)((ObjectMessage) qMsg).getObject();

			mailId = umdd.getMailId();
			personId = umdd.getPersonId();

			if (log.isDebugEnabled())
				log.debug("Delivering mailId:" + mailId + " to personId:" + personId);
			
			try
			{
				this.deliverator.deliver(mailId, personId);
				qMsg.acknowledge();
			}
			catch (NotFoundException ex)
			{
				// Just log a warning and accept the JMS message
				if (log.isErrorEnabled())
					log.error("Unknown mailId(" + mailId + ") or personId(" + personId + ")", ex);
			}
			catch (Exception e)
			{
				if (log.isErrorEnabled())
					log.error("Error processing message!", e);				
			}
		}
		catch (JMSException ex)
		{
			if (log.isErrorEnabled())
				log.error("Error getting data out of message.",ex);
		}
	}
}

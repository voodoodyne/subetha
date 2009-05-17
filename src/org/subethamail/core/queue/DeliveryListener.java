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
		try
		{
			DeliveryQueueItem umdd = (DeliveryQueueItem)((ObjectMessage)qMsg).getObject();

			Long mailId = umdd.getMailId();
			Long personId = umdd.getPersonId();

			if (log.isDebugEnabled())
				log.debug("Delivering mailId:" + mailId + " to personId:" + personId);
			
			try
			{
				this.deliverator.deliver(mailId, personId);
			}
			catch (NotFoundException ex)
			{
				// Just log a warning and accept the JMS message; this is a legit case
				// when mail gets deleted.
				if (log.isWarnEnabled())
					log.warn("Unknown mailId(" + mailId + ") or personId(" + personId + ")", ex);
			}
			
			// This is not supposed to be relevant for MDBs
			//qMsg.acknowledge();
		}
		catch (JMSException ex)
		{
			if (log.isErrorEnabled())
				log.error("Error getting data out of message.", ex);
			
			throw new RuntimeException(ex);
		}
	}
}

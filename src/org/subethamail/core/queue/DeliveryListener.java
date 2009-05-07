/*
 * $Id: $
 * $URL:  $
 */


package org.subethamail.core.queue;

import javax.ejb.MessageDriven;
import javax.inject.Current;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.util.EntityManipulatorBean;

/**
 * Processes delivery queue messages by creating an actual STMP message
 * using JavaMail, relaying it through the deliverator.
 */
@MessageDriven
public class DeliveryListener extends EntityManipulatorBean implements MessageListener{
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
			MailDelivery umdd = (MailDelivery)((ObjectMessage) qMsg).getObject();

			Long mailId = umdd.getMailId();
			Long personId = umdd.getPersonId();
			if (log.isDebugEnabled())
				log.debug("Delivering mailId " + mailId + " to personId " + personId);
	
			try
			{
				this.deliverator.deliver(mailId, personId);
			}
			catch (NotFoundException ex)
			{
				// Just log a warning and accept the JMS message
				if (log.isWarnEnabled())
					log.warn("Unknown mailId(" + mailId + ") or personId(" + personId + ")", ex);
			}	
		}
		catch (JMSException ex)
		{
			log.error("Error getting data off queue",ex);
		}
	}
}

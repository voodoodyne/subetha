/*
 * $Id: $
 * $URL:  $
 */

package org.subethamail.core.queue;

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
import org.subethamail.core.deliv.i.Deliverator;

/**
 * Processes delivery queue messages by creating an actual STMP message
 * using JavaMail, relaying it through the {@link Deliverator}.
 */
//@MessageDriven	// declared in resin-web.xml
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Log
public class DeliveryListener implements MessageListener
{
	/** */
	@Inject Deliverator deliverator;

	/**
	 */
	public void onMessage(Message qMsg)
	{
		try
		{
			DeliveryQueueItem item = (DeliveryQueueItem)((ObjectMessage) qMsg).getObject();

			Long mailId = item.getMailId();
			Long personId = item.getPersonId();

			log.log(Level.FINE,"Delivering mailId:{0} to personId:{1}", new Object[]{mailId, personId});
			
			try
			{
				this.deliverator.deliver(mailId, personId);
			}
			catch (NotFoundException ex)
			{
                // Just log a warning and accept the JMS message
				// It possible the message/subscription has been deleted since we queued the orig request.
                LogRecord logRecord=new LogRecord(Level.WARNING, "Unknown mailId({0}) or personId({1})");
                logRecord.setParameters(new Object[]{mailId, personId});
                logRecord.setThrown(ex);
				log.log(logRecord);
			}
		}
		catch (JMSException ex)
		{
				log.log(Level.SEVERE,"Error getting data out of message.", ex);
			
			throw new RuntimeException(ex);
		}
	}
}

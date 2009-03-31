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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.deliv.i.Deliverator;
import org.subethamail.core.util.EntityManipulatorBean;

@MessageDriven
public class DeliveryListener extends EntityManipulatorBean implements MessageListener{
	/** */
	private static Log log = LogFactory.getLog(DeliveryListener.class);

	/** */
	@Current Deliverator deliverator;

	@Override
	public void onMessage(Message qMsg) {
		try {
			UserMailDeliveryData umdd = (UserMailDeliveryData)((ObjectMessage) qMsg).getObject();

			Long mailId = umdd.getMailId();
			Long personId = umdd.getUserId();
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
		} catch (JMSException e) {
			log.error("Error getting data off queue",e);
		}
	}
}

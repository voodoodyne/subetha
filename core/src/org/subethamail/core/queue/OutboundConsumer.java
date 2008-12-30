/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import java.util.List;

import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Consumer;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.deliv.i.Deliverator;

/**
 * Consumer of the outbound queue.  This is a JBoss message-driven POJO.
 *
 * @author Jeff Schnitzer
 */
@Consumer(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/outbound") // Must be set to this value for some reason
	}
)
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class OutboundConsumer implements Outbound
{
	/** */
	private static Log log = LogFactory.getLog(InboundConsumer.class);

	/** */
	@EJB Deliverator deliverator;

	/**
	 * @see Outbound#deliver(Long, Long)
	 */
	public void deliver(Long mailId, Long personId)
	{
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

	/**
	 * @see Outbound#deliver(Long, List)
	 */
	public void deliver(Long mailId, List<Long> personIds)
	{
		if (log.isDebugEnabled())
			log.debug("Delivering mailId " + mailId + " to personIds " + personIds);

		for (Long personId: personIds)
		{
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
	}
}


/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.ejb.ActivationConfigProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Consumer;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.queue.i.Queuer;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.dao.DAO;

/**
 * Consumer of the inbound queue.  This is a JBoss message-driven POJO.
 * 
 * @author Jeff Schnitzer
 */
@Consumer(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/subetha/inbound")
	}
)
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class InboundConsumer implements Inbound
{
	/** */
	private static Log log = LogFactory.getLog(InboundConsumer.class);
	
	/** */
	@EJB Queuer queuer;
	@EJB DAO dao;
	
	/**
	 * @see Inbound#deliver(Long)
	 */
	public void deliver(Long mailId)
	{
		if (log.isDebugEnabled())
			log.debug("Distributing mailId " + mailId);
		
		Mail mail;
		try
		{
			mail = this.dao.findMail(mailId);
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
		
		// Lookup all recipients and queue the mailId, recipientId pair
		for (Subscription sub: mail.getList().getSubscriptions())
		{
			if (sub.getDeliverTo() != null)
				this.queuer.queueForDelivery(mailId, sub.getPerson().getId());
		}
	}
}


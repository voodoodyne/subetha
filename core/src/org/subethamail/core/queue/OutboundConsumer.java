/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.queue;

import javax.annotation.EJB;
import javax.ejb.ActivationConfigProperty;

import org.jboss.annotation.ejb.Consumer;
import org.subethamail.core.deliv.i.Deliverator;

/**
 * Consumer of the outbound queue.  This is a JBoss message-driven POJO.
 * 
 * @author Jeff Schnitzer
 */
@Consumer(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/subetha/outbound")
	}
)
public class OutboundConsumer implements Outbound
{
	/** */
	@EJB Deliverator deliverator;
	
	/**
	 * @see Outbound#deliver(Long, Long)
	 */
	public void deliver(Long mailId, Long personId)
	{
		this.deliverator.deliver(mailId, personId);
	}
}


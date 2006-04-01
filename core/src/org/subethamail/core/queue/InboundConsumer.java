/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.queue;

import javax.annotation.EJB;
import javax.ejb.ActivationConfigProperty;

import org.jboss.annotation.ejb.Consumer;
import org.subethamail.core.queue.i.Queuer;

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
public class InboundConsumer implements Inbound
{
	/** */
	@EJB Queuer queuer;
	
	/**
	 * @see Inbound#deliver(Long)
	 */
	public void deliver(Long mailId)
	{
		// Lookup all recipients and queue the mailId, recipientId pair
	}
}


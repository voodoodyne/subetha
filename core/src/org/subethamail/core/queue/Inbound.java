/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import javax.ejb.Local;

import org.jboss.annotation.ejb.Producer;
import org.subethamail.core.queue.i.Queuer;

/**
 * Producer interface for the inbound queue.  Call this
 * method as a message-driven POJO.
 * 
 * @author Jeff Schnitzer
 */
@Local
@Producer(connectionFactory="java:/JmsXA")
public interface Inbound
{
	/** */
	public static final String JNDI_NAME = "org.subethamail.core.queue.Inbound";
	
	/**
	 * @see Queuer#queueForDelivery(Long)
	 */
	public void deliver(Long mailId);
}


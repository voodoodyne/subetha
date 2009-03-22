/*
 * $Id: Inbound.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/queue/Inbound.java $
 */

package org.subethamail.core.queue;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Producer;
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


/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import javax.ejb.Local;

import org.jboss.annotation.ejb.Producer;

/**
 * @author Jeff Schnitzer
 */
@Local
@Producer(connectionFactory="java:/JmsXA")
public interface Outbound
{
	/** */
	public static final String JNDI_NAME = "org.subethamail.core.queue.Outbound";
	
	/**
	 */
	public void deliver(Long mailId, Long personId);
}


/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue;

import java.util.List;

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
	 * Deliver the message to the person
	 */
	public void deliver(Long mailId, Long personId);
	
	/**
	 * Deliver the message to the people
	 */
	public void deliver(Long mailId, List<Long> personIds);
}


/*
 * $Id: Outbound.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/queue/Outbound.java $
 */

package org.subethamail.core.queue;

import java.util.List;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Producer;

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


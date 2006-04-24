/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i.helper;

import org.jboss.annotation.ejb.Management;

/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
@Management
public interface Lifecycle
{
	/**
	 * Called when the service starts (usually at deployment).
	 */
	public void start() throws Exception;
	
	/**
	 * Called when the service stops (usually when undeploying).
	 */
	public void stop();
}

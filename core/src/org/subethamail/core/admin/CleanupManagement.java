/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

import org.jboss.annotation.ejb.Management;

/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
@Management
public interface CleanupManagement
{
	/**
	 */
	public void start() throws Exception;
	
	/**
	 */
	public void stop() throws Exception;
	
	/**
	 * Purges all obsolete held messages and subscriptions.
	 */
	public void cleanup();
}

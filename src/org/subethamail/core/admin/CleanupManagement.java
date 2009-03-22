/*
 * $Id: CleanupManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Management;

/**
 * Interface for purging obsolete message and subscription holds.
 * 
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
@Management
@Local
public interface CleanupManagement
{
	/** */
	public static final String JNDI_NAME = "subetha/Cleanup/local";

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

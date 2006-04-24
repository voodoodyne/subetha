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
public interface BootstrapperManagement
{
	/**
	 * When the service starts, make sure there is at least one
	 * siteAdmin account.
	 */
	public void start() throws Exception;
	
	/**
	 * Ensures that the default site admin account exists.  This will
	 * only be called the first time the application runs, unless it
	 * is manually triggered from the jmx console.
	 * 
	 * TODO:  consider, should this reset an existing account to default
	 * values?
	 */
	public void bootstrap();
}

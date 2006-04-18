/*
 * $Id: StatsUpdateServiceMBean.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.smtp;

import org.jboss.annotation.ejb.Management;

/**
 * JMX Management interface for the SMTPService.  The start() and
 * stop() methods are magically called by JBoss.
 * 
 * @author Jeff Schnitzer
 */
@Management
public interface SMTPManagement
{
	/**
	 * Called when the service starts.
	 */
	public void start() throws Exception;
	
	/**
	 * Called when the service stops.
	 */
	public void stop();
}

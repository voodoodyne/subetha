/*
 * $Id: StatsUpdateServiceMBean.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.pluginapi.helper;

import org.jboss.annotation.ejb.Management;

/**
 * Management interface for the AbstractPluginFactory.  Having
 * this interface causes JBoss to call the lifecycle methods.
 *
 * @author Jeff Schnitzer
 */
@Management
public interface AbstractPluginFactoryManagement
{
	/**
	 * Called upon deployment.  Registers the factory with the PluginRegistry.
	 */
	public void start() throws Exception;
}

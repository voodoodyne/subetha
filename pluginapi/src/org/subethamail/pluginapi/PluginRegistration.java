/*
 * $Id: StatsUpdate.java 121 2006-03-07 09:50:09Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.pluginapi;

import javax.ejb.Local;


/**
 * This local interface allows plugin factories register themsleves with the
 * application.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface PluginRegistration
{
	/**
	 * Register a plugin factory.  Factories must register themselves
	 * when the application deploys, using a JBoss service start() method.
	 */
	public void register(PluginFactory factory);
	
	/**
	 * Deregister a plugin factory.  Factories should remove themselves
	 * when they undeploy, using a JBoss service stop() method.  If you
	 * don't plan on doing a lot of hot deployment or redeployment of
	 * plugins, this is not a critical method.
	 */
	public void deregister(PluginFactory factory);
}

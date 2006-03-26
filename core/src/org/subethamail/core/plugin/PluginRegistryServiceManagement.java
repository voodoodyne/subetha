/*
 * $Id: StatsUpdateServiceMBean.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin;

import org.jboss.annotation.ejb.Management;

/**
 *
 * @author Jeff Schnitzer
 */
@Management
public interface PluginRegistryServiceManagement
{
	/**
	 * Called upon deployment.
	 */
	public void create() throws Exception;
}

/*
 * $Id: StatsUpdate.java 121 2006-03-07 09:50:09Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin;

import java.util.Collection;

import javax.ejb.Local;

import org.subethamail.pluginapi.PluginFactory;

/**
 * Internal local interface allows the core application to query
 * the plugin registry.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface PluginRegistry
{
	/**
	 * @return all registered plugin factories.
	 */
	public Collection<PluginFactory> getFactories();
	
	/**
	 * @return the plugin factory with the specified class name, or null
	 *  if no factory was registered with that name.
	 */
	public PluginFactory getFactory(String className);
}

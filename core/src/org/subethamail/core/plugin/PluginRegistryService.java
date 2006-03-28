/*
 * $Id: StatsUpdateService.java 121 2006-03-07 09:50:09Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManager.java,v $
 */

package org.subethamail.core.plugin;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginRegistration;


/**
 * Tracks all available plugins.  Plugins register themselves with
 * this service upon deployment.
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=PluginRegistry")
public class PluginRegistryService implements PluginRegistration, PluginRegistry
{
	/** */
	private static Log log = LogFactory.getLog(PluginRegistryService.class);
	
	/**
	 * We do have to be a bit careful with synchronization since someone
	 * might hot-deploy some plugins into a running appserver.
	 */
	Map<String, Plugin> plugins = new ConcurrentHashMap<String, Plugin>();

	/**
	 * @see PluginRegistration#register(Plugin)
	 */
	public synchronized void register(Plugin plugin)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + plugin.getClass().getName());
			
		this.plugins.put(plugin.getClass().getName(), plugin);
	}

	/**
	 * @see PluginRegistration#deregister(Plugin)
	 */
	public synchronized void deregister(Plugin plugin)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + plugin.getClass().getName());
			
		this.plugins.remove(plugin.getClass().getName());
	}

	/**
	 * @see PluginRegistry#getPlugins()
	 */
	public Collection<Plugin> getPlugins()
	{
		return this.plugins.values();
	}

	/**
	 * @see PluginRegistry#getPlugins(String)
	 */
	public Plugin getPlugin(String className)
	{
		return this.plugins.get(className);
	}
}

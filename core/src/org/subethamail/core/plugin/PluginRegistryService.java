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
import org.subethamail.pluginapi.PluginFactory;
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
	Map<String, PluginFactory> factories = new ConcurrentHashMap<String, PluginFactory>();

	/**
	 * @see PluginRegistration#register(PluginFactory)
	 */
	public synchronized void register(PluginFactory factory)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + factory.getClass().getName());
			
		this.factories.put(factory.getClass().getName(), factory);
	}

	/**
	 * @see PluginRegistration#deregister(PluginFactory)
	 */
	public synchronized void deregister(PluginFactory factory)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + factory.getClass().getName());
			
		this.factories.remove(factory.getClass().getName());
	}

	/**
	 * @see PluginRegistry#getFactories()
	 */
	public Collection<PluginFactory> getFactories()
	{
		return this.factories.values();
	}

	/**
	 * @see PluginRegistry#getFactory(String)
	 */
	public PluginFactory getFactory(String className)
	{
		return this.factories.get(className);
	}
}

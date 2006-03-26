/*
 * $Id: StatsUpdateService.java 121 2006-03-07 09:50:09Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManager.java,v $
 */

package org.subethamail.core.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class PluginRegistryService implements PluginRegistration, PluginRegistry, PluginRegistryServiceManagement
{
	/** */
	private static Log log = LogFactory.getLog(PluginRegistryService.class);
	
	/**
	 * This shouldn't need to be synchronized because writes are done
	 * in a single thread at application deployment.  Reads are multithreaded
	 * but nobody should be registering at that point.
	 */
	Map<String, PluginFactory> factories = new HashMap<String, PluginFactory>();

	/**
	 * @see PluginRegistration#register(PluginFactory)
	 */
	public void register(PluginFactory factory)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + factory.getClass().getName());
			
		this.factories.put(factory.getClass().getName(), factory);
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

	/**
	 * 
	 */
	public void create() throws Exception
	{
		log.debug("####### creating service");
	}
}

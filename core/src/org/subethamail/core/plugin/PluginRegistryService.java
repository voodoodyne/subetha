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
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.PluginRegistration;


/**
 * Tracks all available plugins.  Plugins register themselves with
 * this service upon deployment.  Note that this object needs to be
 * thread-safe because plugins can be registered and deregistered
 * into a running environment.
 * 
 * @author Jeff Schnitzer
 */
@Service(name="PluginRegistry", objectName="subetha:service=PluginRegistry")
public class PluginRegistryService implements PluginRegistration, PluginRegistry
{
	/** */
	private static Log log = LogFactory.getLog(PluginRegistryService.class);
	
	/**
	 * Key is filter classname.
	 */
	Map<String, Filter> filters = new ConcurrentHashMap<String, Filter>();

	/**
	 * Key is blueprint classname.
	 */
	Map<String, Blueprint> blueprints = new ConcurrentHashMap<String, Blueprint>();

	/**
	 * @see PluginRegistration#register(Filter)
	 */
	public synchronized void register(Filter filter)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + filter.getClass().getName());
			
		this.filters.put(filter.getClass().getName(), filter);
	}

	/**
	 * @see PluginRegistration#deregister(Filter)
	 */
	public synchronized void deregister(Filter filter)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + filter.getClass().getName());
			
		this.filters.remove(filter.getClass().getName());
	}

	/**
	 * @see PluginRegistration#register(Blueprint)
	 */
	public synchronized void register(Blueprint print)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + print.getClass().getName());
			
		this.blueprints.put(print.getClass().getName(), print);
	}

	/**
	 * @see PluginRegistration#deregister(Blueprint)
	 */
	public synchronized void deregister(Blueprint print)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + print.getClass().getName());
			
		this.blueprints.remove(print.getClass().getName());
	}
	
	/**
	 * @see PluginRegistry#getFilters()
	 */
	public Collection<Filter> getFilters()
	{
		return this.filters.values();
	}

	/**
	 * @see PluginRegistry#getFilter(String)
	 */
	public Filter getFilter(String className)
	{
		return this.filters.get(className);
	}

	/**
	 * @see PluginRegistry#getBlueprints()
	 */
	public Collection<Blueprint> getBlueprints()
	{
		return this.blueprints.values();
	}

	/**
	 * @see PluginRegistry#getBlueprint(String)
	 */
	public Blueprint getBlueprint(String className)
	{
		return this.blueprints.get(className);
	}
}

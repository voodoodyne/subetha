/*
 * $Id: StatsUpdate.java 121 2006-03-07 09:50:09Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin;

import java.util.Collection;

import javax.ejb.Local;

import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.Filter;

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
	 * @return all registered filters.
	 */
	public Collection<Filter> getFilters();
	
	/**
	 * @return the filter with the specified class name, or null
	 *  if no filter was registered with that name.
	 */
	public Filter getFilter(String className);

	/**
	 * @return all registered blueuprints.
	 */
	public Collection<Blueprint> getBlueprints();
	
	/**
	 * @return the blueprint with the specified class name, or null
	 *  if no blueprint was registered with that name.
	 */
	public Blueprint getBlueprint(String className);
}

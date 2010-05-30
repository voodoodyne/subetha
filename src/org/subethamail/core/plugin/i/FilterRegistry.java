/*
 * $Id: FilterRegistry.java 263 2006-05-04 20:58:25Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i;


/**
 * Lets clients get a list of filters.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public interface FilterRegistry
{
	/**
	 * @return all the available filters.
	 */
	public Iterable<Filter> getFilters();
	
	/**
	 * Gets the filter instance for the specified class name
	 * @throws some nasty exceptions if the filter isn't registered properly
	 */
	public Filter getFilter(String filterClassName);
}

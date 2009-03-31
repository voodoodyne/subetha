/*
 * $Id: FilterRegistry.java 263 2006-05-04 20:58:25Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i;

import java.util.Set;

import javax.ejb.Local;

/**
 * This local interface allows filters to be registered.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Local
public interface FilterRegistry
{
	/**
	 * Register a mail filter.  The filter will become available
	 * immediately.
	 */
	public void register(String className);
	
	/**
	 * Deregister a mail filter.  The filter will no longer be processed.
	 */
	public void deregister(String className);
	
	/**
	 * @return all the available filters.
	 */
	public Set<String> getFilters();

}

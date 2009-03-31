/*
 * $Id: BlueprintRegistry.java 263 2006-05-04 20:58:25Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i;

import javax.ejb.Local;

/**
 * This local interface allows blueprints to be registered
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Local
public interface BlueprintRegistry
{
	/**
	 * Register a blueprint. The blueprint will become available immediately.
	 */
	public void register(String clazz);

	/**
	 * Deregister a blueprint.
	 */
	public void deregister(String clazz);
}

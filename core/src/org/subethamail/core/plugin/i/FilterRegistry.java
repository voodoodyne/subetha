/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i;

import javax.ejb.Local;




/**
 * This local interface allows filters to register themsleves with the
 * application.  Plugins should register themselves when the application deploys
 * and de-register themselves when the app undeploys.  The JBoss service
 * lifecycle methods start() and stop() can be used.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface FilterRegistry
{
	/**
	 * Register a mail filter.  The filter will become available
	 * immediately.
	 */
	public void register(Filter filter);
	
	/**
	 * Deregister a mail filter.  The filter will no longer be processed.
	 */
	public void deregister(Filter filter);
}

/*
 * $Id: Lifecycle.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.plugin.i.helper;


/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
//@Management
public interface Lifecycle
{
	/**
	 * Called when the service starts (usually at deployment).
	 */
	public void start() throws Exception;
	
	/**
	 * Called when the service stops (usually when undeploying).
	 */
	public void stop();
}

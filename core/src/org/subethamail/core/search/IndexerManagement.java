/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.search;

import org.jboss.ejb3.annotation.Management;

/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
@Management
public interface IndexerManagement
{
	/**
	 */
	public void start() throws Exception;
	
	/**
	 */
	public void stop() throws Exception;

	/**
	 * Rebuilds the entire index from scratch.  Searches will still
	 * be serviced from the (old) index while the new index is being
	 * built.  VERY EXPENSIVE.
	 */
	public void rebuild();
	
	/**
	 * Updates the index with any changes in since the last update.
	 */
	public void update();
}

/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

import org.jboss.ejb3.annotation.Management;

/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jon Stevens
 */
@Management
public interface VelocityManagement
{
	/**
	 * Initializes Velocity
	 */
	public void start() throws Exception;
}

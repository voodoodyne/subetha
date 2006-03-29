/*
 * $Id: StatsUpdateServiceMBean.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

import org.jboss.annotation.ejb.Management;

/**
 * Management interface that provides lifecycle callback methods.
 * Implementing this interface on a Service bean causes JBoss to
 * magically call the methods.
 *
 * @author Jeff Schnitzer
 */
@Management
public interface EncryptorManagement
{
	/**
	 * When the service starts, it checks to make sure there is
	 * a valid key.
	 */
	public void start() throws Exception;
}

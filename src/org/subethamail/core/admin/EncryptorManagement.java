/*
 * $Id: EncryptorManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

import org.jboss.ejb3.annotation.Management;

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

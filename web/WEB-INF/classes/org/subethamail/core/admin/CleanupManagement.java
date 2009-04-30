/*
 * $Id: CleanupManagement.java 988 2008-12-30 08:51:13Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */

package org.subethamail.core.admin;

/**
 * Interface for purging obsolete message and subscription holds.
 * Used as MBean interface.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public interface CleanupManagement
{	
	/**
	 * Purges all obsolete held messages and subscriptions.
	 */
	public void cleanup();
}

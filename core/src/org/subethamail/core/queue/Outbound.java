/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.queue;

import javax.ejb.Local;

import org.jboss.annotation.ejb.Producer;

/**
 * @author Jeff Schnitzer
 */
@Local
@Producer
public interface Outbound
{
	/** */
	public static final String JNDI_NAME = "org.subethamail.core.queue.Outbound";
	
	/**
	 */
	public void deliver(Long mailId, Long personId);
}


/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.queue;

import javax.ejb.Stateless;

import org.subethamail.core.queue.i.Queuer;
import org.subethamail.core.queue.i.QueuerRemote;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Queuer")
//@SecurityDomain("subetha")
//@RunAs("god")
public class QueuerEJB implements Queuer, QueuerRemote
{
	/**
	 * @see Queuer#queueForDelivery(Long)
	 */
	public void queueForDelivery(Long mailId)
	{
		
	}
	
	/**
	 * @see Queuer#queueForDelivery(Long, Long)
	 */
	public void queueForDelivery(Long mailId, Long personId)
	{
		
	}
}


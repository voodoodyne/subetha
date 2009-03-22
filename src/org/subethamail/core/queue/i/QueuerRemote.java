/*
 * $Id: QueuerRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/queue/i/QueuerRemote.java $
 */

package org.subethamail.core.queue.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
@Remote
public interface QueuerRemote extends Queuer
{
	/** */
	public static final String JNDI_NAME = "subetha/Queuer/remote";
}

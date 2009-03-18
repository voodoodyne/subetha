/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.queue.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
//@Remote
public interface QueuerRemote extends Queuer
{
	/** */
	public static final String JNDI_NAME = "subetha/Queuer/remote";
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.deliv.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
@Remote
public interface DeliveratorRemote extends Deliverator
{
	/** */
	public static final String JNDI_NAME = "subetha/Deliverator/remote";
}

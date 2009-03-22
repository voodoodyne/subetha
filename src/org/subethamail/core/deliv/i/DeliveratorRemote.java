/*
 * $Id: DeliveratorRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/deliv/i/DeliveratorRemote.java $
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

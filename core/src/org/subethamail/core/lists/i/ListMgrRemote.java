/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
@Remote
public interface ListMgrRemote extends ListMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/ListMgr/remote";
}

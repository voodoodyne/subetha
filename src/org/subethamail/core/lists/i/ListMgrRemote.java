/*
 * $Id: ListMgrRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/ListMgrRemote.java $
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

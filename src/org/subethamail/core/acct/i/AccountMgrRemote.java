/*
 * $Id: AccountMgrRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.acct.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
@Remote
public interface AccountMgrRemote extends AccountMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/AccountMgr/remote";
}

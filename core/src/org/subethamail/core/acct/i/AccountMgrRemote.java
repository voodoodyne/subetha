/*
 * $Id$
 * $URL$
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

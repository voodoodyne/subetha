/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.injector.i;

import javax.ejb.Remote;


/**
 * @author Jeff Schnitzer
 */
@Remote
public interface InjectorRemote extends Injector
{
	/** */
	public static final String JNDI_NAME = "subetha/InjectorEJB/remote";
}

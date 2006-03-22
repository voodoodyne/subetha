/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.injector;

import javax.annotation.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.entity.dao.DAO;

/**
 * @author Jeff Schnitzer
 */
@Stateless
//@SecurityDomain("subetha")
//@RunAs("god")
public class InjectorEJB implements Injector, InjectorRemote
{
	/** */
	private static Log log = LogFactory.getLog(InjectorEJB.class);

	/** */
	@EJB DAO dao;

	/**
	 * @see Injector#inject(String)
	 */
	public void inject(byte[] mail)
	{
	}
}

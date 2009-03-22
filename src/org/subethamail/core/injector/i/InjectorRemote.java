/*
 * $Id: InjectorRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/injector/i/InjectorRemote.java $
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
	public static final String JNDI_NAME = "subetha/Injector/remote";
}

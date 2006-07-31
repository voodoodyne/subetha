/*
 * $Id$
 * $URL$
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

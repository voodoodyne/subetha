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
public interface ArchiverRemote extends Archiver
{
	/** */
	public static final String JNDI_NAME = "subetha/Archiver/remote";
}

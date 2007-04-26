/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin.i;

import javax.ejb.Remote;


/**
 */
@Remote
public interface PlumberRemote extends Plumber
{
	/** */
	public static final String JNDI_NAME = "subetha/Plumber/remote";
}

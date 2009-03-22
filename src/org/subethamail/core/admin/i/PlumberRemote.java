/*
 * $Id: PlumberRemote.java 948 2007-04-26 06:27:45Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/PlumberRemote.java $
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

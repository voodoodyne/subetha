/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin.i;

import javax.ejb.Remote;



/**
 * @author Jeff Schnitzer
 */
@Remote
public interface AdminRemote extends Admin
{
	/** */
	public static final String JNDI_NAME = "subetha/Admin/remote";
}

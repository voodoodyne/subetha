/*
 * $Id: ReceptionistRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/ReceptionistRemote.java $
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

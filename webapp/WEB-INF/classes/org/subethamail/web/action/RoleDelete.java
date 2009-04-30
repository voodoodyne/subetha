/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

/**
 * Deletes a role.
 * 
 * @author Jeff Schnitzer
 */
public class RoleDelete extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(RoleDelete.class);

	/** The role to delete */
	@Property Long deleteRoleId;
	
	/** Convert all members who had that role to this role */
	@Property Long convertToRoleId;
	
	/** This is filled in afterwards so we know where to redirect to */
	@Property Long listId;

	/** */
	public void execute() throws Exception
	{
		this.listId = Backend.instance().getListMgr().deleteRole(this.deleteRoleId, this.convertToRoleId);
	}
}

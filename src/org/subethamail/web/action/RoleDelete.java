/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Deletes a role.
 * 
 * @author Jeff Schnitzer
 */
@Log
public class RoleDelete extends AuthAction 
{
	/** The role to delete */
	@Getter @Setter Long deleteRoleId;
	
	/** Convert all members who had that role to this role */
	@Getter @Setter Long convertToRoleId;
	
	/** This is filled in afterwards so we know where to redirect to */
	@Getter @Setter Long listId;

	/** */
	public void execute() throws Exception
	{
		this.listId = Backend.instance().getListMgr().deleteRole(this.deleteRoleId, this.convertToRoleId);
	}
}

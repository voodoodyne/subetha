/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger log = LoggerFactory.getLogger(RoleDelete.class);

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

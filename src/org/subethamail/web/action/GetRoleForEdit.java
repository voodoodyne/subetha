/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.web.Backend;
import org.subethamail.web.action.RoleSave.Model;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Pre-populates a model for RoleSave.
 * 
 * @author Jeff Schnitzer
 */
public class GetRoleForEdit extends AuthAction 
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetRoleForEdit.class);
	
	/** */
	@Getter @Setter Long roleId;
	
	/** */
	public void execute() throws Exception
	{
		Model model = new Model();
		
		RoleData data = Backend.instance().getListMgr().getRole(this.roleId);
		
		model.roleId = data.getId();
		model.listId = data.getListId();
		model.name = data.getName();
		model.realPermissions = data.getPermissions();
		
		this.getCtx().setModel(model);
	}
	
}

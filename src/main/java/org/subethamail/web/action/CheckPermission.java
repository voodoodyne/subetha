/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.entity.i.Permission;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * This action is used to check a permission from the web interface.
 * Requires a listId to be passed into it.
 * 
 * An example of this is in list_settings.jsp where we need to 
 * check whether someone has EDIT_SETTINGS permission because
 * it is actually public data, yet this page really shouldn't
 * be public.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class CheckPermission extends AuthAction
{
	@Getter @Setter Long listId;
	
	/** */
	public void execute() throws Exception
	{
		Permission perm = Permission.valueOf((String)this.getCtx().getActionParams().get("perm"));
		Backend.instance().getListMgr().checkPermission(listId, perm);
	}
}

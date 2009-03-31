/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import javax.inject.Current;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.entity.i.Permission;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

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
	@Current ListMgr listMgr;
	
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CheckPermission.class);
	@Property Long listId;
	
	/** */
	public void execute() throws Exception
	{
		Permission perm = Permission.valueOf((String)this.getCtx().getActionParams().get("perm"));
		listMgr.checkPermission(listId, perm);
	}
}

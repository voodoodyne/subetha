/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.core.acct.i.MyListRelationship;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.entity.i.Permission;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.util.PermissionWrapper;

/**
 * Gets data about a mailing list and the current user.
 * Note that the permissions are wrapped into a form
 * more useful to JSPs.
 * 
 * @author Jeff Schnitzer
 */
public class GetMyListRelationship extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;
	@Getter @Setter String listName;
	@Getter @Setter String listEmail;
	@Getter @Setter PermissionWrapper perms;
	@Getter @Setter boolean subscribed;
	@Getter @Setter String deliverTo;
	@Getter @Setter RoleData role;
	
	@Getter @Setter Set<Permission> rawPerms;
		
	/** */
	public void execute() throws Exception
	{
		MyListRelationship data = Backend.instance().getAccountMgr().getMyListRelationship(this.listId);
		this.listName = data.getListName();
		this.listEmail = data.getListEmail();
		this.perms = new PermissionWrapper(data.getPerms());
		this.subscribed = data.isSubscribed();
		this.deliverTo = data.getDeliverTo();
		this.role = data.getRole();
		
		this.rawPerms = data.getPerms();
	}
}

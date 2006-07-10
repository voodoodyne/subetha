/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.MyListRelationship;
import org.subethamail.entity.i.Permission;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.util.PermissionWrapper;
import org.tagonist.propertize.Property;

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
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GetMyListRelationship.class);
	
	/** */
	@Property Long listId;
	@Property String listName;
	@Property String listEmail;
	@Property PermissionWrapper perms;
	@Property boolean subscribed;
	@Property String deliverTo;
	
	@Property Set<Permission> rawPerms;
		
	/** */
	public void execute() throws Exception
	{
		MyListRelationship data = Backend.instance().getAccountMgr().getMyListRelationship(this.listId);
		this.listName = data.getListName();
		this.listEmail = data.getListEmail();
		this.perms = new PermissionWrapper(data.getPerms());
		this.subscribed = data.isSubscribed();
		this.deliverTo = data.getDeliverTo();
		
		this.rawPerms = data.getPerms();
	}
}

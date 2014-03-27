/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.subethamail.entity.i.Permission;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Gets a number of statistics about a list, depending on user permissions.
 * Permissions must be passed as an action param.
 * 
 * The properties of this object might be null if the appropriate permission
 * is not set.
 * 
 * @author Jeff Schnitzer
 */
public class GetListStats extends AuthAction 
{
	/** */
	@Getter @Setter Long listId;
	
	@Getter @Setter Integer subscriberCount;
	@Getter @Setter Integer archiveCount;
	@Getter @Setter Integer heldSubscriptionCount;
	@Getter @Setter Integer heldMessageCount;
	
	/** */
	public void execute() throws Exception
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<Permission> perms = (Set)this.getCtx().getActionParams().get("perms");
		
		if (perms.contains(Permission.VIEW_SUBSCRIBERS))
			this.subscriberCount = Backend.instance().getListMgr().countSubscribers(this.listId);
		
		if (perms.contains(Permission.VIEW_ARCHIVES))
			this.archiveCount = Backend.instance().getArchiver().countMailByList(this.listId);
		
		if (perms.contains(Permission.APPROVE_SUBSCRIPTIONS))
			this.heldSubscriptionCount = Backend.instance().getListMgr().countHeldSubscriptions(this.listId);
		
		if (perms.contains(Permission.APPROVE_MESSAGES))
			this.heldMessageCount = Backend.instance().getListMgr().countHeldMessages(this.listId);
	}
	
}

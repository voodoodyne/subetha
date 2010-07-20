/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.entity.i.Permission;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.tagonist.propertize.Property;

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
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(GetListStats.class);
	
	/** */
	@Property Long listId;
	
	@Property Integer subscriberCount;
	@Property Integer archiveCount;
	@Property Integer heldSubscriptionCount;
	@Property Integer heldMessageCount;
	
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

/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(GetListStats.class);
	
	/** */
	@Property Long listId;
	
	@Property Integer subscriberCount;
	@Property Integer archiveCount;
	@Property Integer heldMessageCount;
	
	/** */
	@SuppressWarnings("unchecked")
	public void execute() throws Exception
	{
		Set<Permission> perms = (Set)this.getCtx().getActionParams().get("perms");
		
		if (perms.contains(Permission.VIEW_SUBSCRIBERS))
			this.subscriberCount = Backend.instance().getListMgr().countSubscribers(this.listId);
		
		if (perms.contains(Permission.READ_ARCHIVES))
			this.archiveCount = Backend.instance().getArchiver().countMailByList(this.listId);
		
		if (perms.contains(Permission.APPROVE_MESSAGES))
			this.heldMessageCount = Backend.instance().getListMgr().countHeldMessages(this.listId);
	}
	
}

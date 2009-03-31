/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Current;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;
import org.subethamail.plugin.filter.AppendFooterFilter;
import org.subethamail.plugin.filter.ListHeaderFilter;
import org.subethamail.plugin.filter.ReplyToFilter;

/**
 * Creates a list suitable for a small social group. 
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 * @author Scott Hernandez
 */
public class SocialBlueprint implements Blueprint
{
	@Current ListMgr listMgr;

	/** */
	public String getName()
	{
		return "Social List";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a list suitable for social groups.  Subscriptions must" +
			" be approved by moderators but any subscriber may post.  Reply-To" +
			" will be set back to the list.";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		try
		{
			listMgr.setHoldSubscriptions(listId, true);
			
			// Subscriber
			Set<Permission> perms = new HashSet<Permission>();
			perms.add(Permission.POST);
			perms.add(Permission.VIEW_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			perms.add(Permission.VIEW_SUBSCRIBERS);
			Long roleId = listMgr.addRole(listId, "Subscriber", perms);
			listMgr.setDefaultRole(listId, roleId);
			
			// Guest
			perms = new HashSet<Permission>();
			roleId = listMgr.addRole(listId, "Guest", perms);
			listMgr.setAnonymousRole(listId, roleId);
	
			// Moderator
			perms = new HashSet<Permission>();
			perms.add(Permission.POST);
			perms.add(Permission.VIEW_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			perms.add(Permission.VIEW_SUBSCRIBERS);
			perms.add(Permission.APPROVE_MESSAGES);
			perms.add(Permission.APPROVE_SUBSCRIPTIONS);
			perms.add(Permission.VIEW_ROLES);
			listMgr.addRole(listId, "Moderator", perms);

			// Add a couple useful filters
			listMgr.setFilterDefault(listId, ReplyToFilter.class.getName());
			listMgr.setFilterDefault(listId, AppendFooterFilter.class.getName());
			listMgr.setFilterDefault(listId, ListHeaderFilter.class.getName());
		}
		catch (PermissionException pe)
		{
			throw new RuntimeException(pe);
		}
		catch (NotFoundException nfe)
		{
			throw new RuntimeException(nfe);
		}
		
	}
}
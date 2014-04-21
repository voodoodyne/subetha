/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;
import org.subethamail.plugin.filter.AppendFooterFilter;
import org.subethamail.plugin.filter.ListHeaderFilter;
import org.subethamail.plugin.filter.ReplyToFilter;

/**
 * Creates a list suitable for a publicly advertised technical list.
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Singleton
public class TechnicalBlueprint implements Blueprint
{
	@Inject ListMgr listMgr;

	/** */
	public String getName()
	{
		return "Technical List";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a list suitable for public technical support.  Anyone can " +
			" subscribe and view archives, but email addresses are hidden from" +
			" nonsubscribers to avoid poaching by spam bots. By default replies" +
			" go back to the list and the subscriber list is only available to" +
			" the list owner(s).";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		try
		{
			// Subscriber
			Set<Permission> perms = new HashSet<Permission>();
			perms.add(Permission.POST);
			perms.add(Permission.VIEW_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			Long roleId = listMgr.addRole(listId, "Subscriber", perms);
			listMgr.setDefaultRole(listId, roleId);
			
			// Guest
			perms = new HashSet<Permission>();
			perms.add(Permission.VIEW_ARCHIVES);
			roleId = listMgr.addRole(listId, "Guest", perms);
			listMgr.setAnonymousRole(listId, roleId);

			// Add a couple useful footers
			listMgr.setFilterDefault(listId, AppendFooterFilter.class.getName());
			listMgr.setFilterDefault(listId, ListHeaderFilter.class.getName());
			listMgr.setFilterDefault(listId, ReplyToFilter.class.getName());
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
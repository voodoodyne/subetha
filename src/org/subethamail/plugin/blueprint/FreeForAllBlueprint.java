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

/**
 * Creates an announce-only list. 
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Singleton
public class FreeForAllBlueprint implements Blueprint
{
	@Inject ListMgr listMgr;

	/** */
	public String getName()
	{
		return "Free-For-All";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a wide-open list that allows anyone to post and view" +
			" the subscriber list.  Because such a list is likely to attract" +
			" spam, you should only create such a list in an protected intranet" +
			" environment.";
	}
	
	/**	 */
	public void configureMailingList(Long listId)
	{
		try
		{
			Set<Permission> perms = new HashSet<Permission>();
			perms.add(Permission.POST);
			perms.add(Permission.VIEW_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			perms.add(Permission.VIEW_SUBSCRIBERS);
			perms.add(Permission.VIEW_ROLES);
			Long roleId = listMgr.addRole(listId, "Everyone", perms);
			
			listMgr.setDefaultRole(listId, roleId);
			listMgr.setAnonymousRole(listId, roleId);

			// Add a couple useful filters
			listMgr.setFilterDefault(listId, AppendFooterFilter.class.getName());
			listMgr.setFilterDefault(listId, ListHeaderFilter.class.getName());
		}
		catch (NotFoundException nfe)
		{
			throw new RuntimeException(nfe);
		}
		catch (PermissionException pe)
		{
			throw new RuntimeException(pe);
		}
		
	}
}
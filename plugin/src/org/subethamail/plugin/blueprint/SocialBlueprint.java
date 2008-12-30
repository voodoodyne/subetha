/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;
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
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class SocialBlueprint extends AbstractBlueprint implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	@EJB ListMgr listMgr;

	public void start() throws Exception
	{
		super.start();
	}

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

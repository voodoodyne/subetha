/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.PermissionException;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;
import org.subethamail.plugin.filter.ReplyToFilter;

/**
 * Creates a list suitable for a publicly advertised technical list.
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class TechnicalBlueprint extends AbstractBlueprint implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	@EJB ListMgr listMgr;

	public void start() throws Exception
	{
		super.start();

		if (this.listMgr != null)
			throw new RuntimeException("JBoss fixed, this code can be removed now");
		else
		{
			Context ctx = new InitialContext();
			this.listMgr = (ListMgr)ctx.lookup("subetha/ListMgr/local");
		}		
	}

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
			" nonsubscribers to avoid poaching by spam bots.  The subscriber list" +
			" is only available to the list owner(s).";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		// Setup Roles
		try
		{
			// Subscriber
			Set<Permission> perms = new HashSet<Permission>();
			perms.add(Permission.POST);
			perms.add(Permission.READ_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			Long roleId = listMgr.addRole(listId, "Subscriber", perms);
			listMgr.setDefaultRole(listId, roleId);
			
			// Guest
			perms = new HashSet<Permission>();
			perms.add(Permission.READ_ARCHIVES);
			perms.add(Permission.VIEW_SUBSCRIBERS);
			roleId = listMgr.addRole(listId, "Guest", perms);
			listMgr.setAnonymousRole(listId, roleId);
		}
		catch(PermissionException pe)
		{
			throw new RuntimeException(pe);
		}
		catch(NotFoundException nfe)
		{
			throw new RuntimeException(nfe);
		}
		
	}
}

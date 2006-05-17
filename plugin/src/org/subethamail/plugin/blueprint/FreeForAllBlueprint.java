/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.common.PermissionException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Creates an announce-only list. 
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class FreeForAllBlueprint extends AbstractBlueprint implements Lifecycle
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
			perms.add(Permission.READ_ARCHIVES);
			perms.add(Permission.VIEW_ADDRESSES);
			perms.add(Permission.VIEW_SUBSCRIBERS);
			Long roleId = listMgr.addRole(listId, "Everyone", perms);
			
			listMgr.setDefaultRole(listId, roleId);
			listMgr.setAnonymousRole(listId, roleId);
		}
		catch(NotFoundException nfe)
		{
			throw new RuntimeException(nfe);
		}
		catch(PermissionException pe)
		{
			throw new RuntimeException(pe);
		}
		
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.List;

/**
 * Information about a mailing list's roles.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class ListRoles implements Serializable
{
	Long listId;
	RoleData defaultRole;
	RoleData anonymousRole;
	List<RoleData> roles;
	
	/**
	 */
	public ListRoles(Long listId, RoleData defaultRole, RoleData anonymousRole, List<RoleData> roles)
	{
		this.listId = listId;
		this.defaultRole = defaultRole;
		this.anonymousRole = anonymousRole;
		this.roles = roles;
	}

	/** */
	public RoleData getAnonymousRole()
	{
		return this.anonymousRole;
	}

	/** */
	public RoleData getDefaultRole()
	{
		return this.defaultRole;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
	}

	/** */
	public List<RoleData> getRoles()
	{
		return this.roles;
	}
	
}

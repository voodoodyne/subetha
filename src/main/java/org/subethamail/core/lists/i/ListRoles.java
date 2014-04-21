package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.List;

/**
 * Information about a mailing list's roles.
 *
 * @author Jeff Schnitzer
 */
public class ListRoles implements Serializable
{
	private static final long serialVersionUID = 1L;

	Long listId;
	RoleData defaultRole;
	RoleData anonymousRole;
	List<RoleData> roles;
	
	protected ListRoles()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

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

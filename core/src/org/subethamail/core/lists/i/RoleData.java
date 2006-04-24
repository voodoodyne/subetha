/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;
import java.util.Set;

import org.subethamail.common.Permission;

/**
 * Information about a role.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class RoleData implements Serializable
{
	Long id;
	String name;
	boolean owner;
	Set<Permission> permissions;
	Long listId;
	
	/**
	 */
	public RoleData(Long id, String name, boolean owner, Set<Permission> permissions, Long listId)
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.permissions = permissions;
		this.listId = listId;
	}

	/** */
	public Long getId()
	{
		return this.id;
	}

	/** */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Is this the special owner role for the list? 
	 */
	public boolean isOwner()
	{
		return this.owner;
	}

	/** */
	public Set<Permission> getPermissions()
	{
		return this.permissions;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
	}
	
}

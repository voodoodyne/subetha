/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
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
	
	/**
	 */
	public RoleData(Long id, String name, boolean owner, Set<Permission> permissions)
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.permissions = permissions;
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
	
}

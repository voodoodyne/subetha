/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;
import java.util.Set;

import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.entity.i.Permission;


/**
 * Information about a mailing list and the subscription status
 * information for a person.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MySubscription implements Serializable
{
	/** */
	ListData list;
	String deliverTo;
	boolean subscribed;
	boolean owner;
	RoleData role;
	Set<Permission> perms;
	
	/**
	 */
	public MySubscription(
					ListData list,
					String deliverTo,
					boolean subscribed,
					boolean owner,
					RoleData role,
					Set<Permission> permissions)
	{
		this.list = list;
		this.deliverTo = deliverTo;
		this.subscribed = subscribed;
		this.owner = owner;
		this.role = role;
		this.perms = permissions;
	}

	/** */
	public String toString()
	{
		return this.getClass().getName() + " {list=" + this.list + "}";
	}

	/** The name of the role of the subscription */
	public String getRoleName()
	{
		return this.role.getName();
	}
	
	/** The role of the subscription */
	public RoleData getRole()
	{
		return this.role;
	}
	
	/**
	 * The email address that mail gets delivered to, or null if no
	 * delivery is enabled.  Will also be null if user is not subscribed. 
	 */
	public String getDeliverTo()
	{
		return this.deliverTo;
	}

	/** @return whether or not the user has the special Owner role */
	public boolean isOwner()
	{
		return this.owner;
	}

	/**
	 * @return whether or not the user is subscribed.  Not being
	 * logged in means not subscribed. 
	 */
	public boolean isSubscribed()
	{
		return this.subscribed;
	}

	/** 
	 * @return the actual permissions of this user on the list.
	 * Note that list owners and site admins have all permissions.
	 */
	public Set<Permission> getPerms()
	{
		return this.perms;
	}

	/**
	 * @return the mailing list data for this subscription.
	 */
	public ListData getList()
	{
		return this.list;
	}
}

/*
 * $Id: MyListRelationship.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/MyListRelationship.java $
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;
import java.util.Set;

import org.subethamail.entity.i.Permission;


/**
 * Minimal amount of information about a person's permissions on a list.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MyListRelationship implements Serializable
{
	/** */
	Long listId;
	String listName;
	String listEmail;
	Set<Permission> perms;
	boolean subscribed;
	String deliverTo;
	
	protected MyListRelationship()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0		
	}

	/**
	 */
	public MyListRelationship(
					Long listId,
					String listName,
					String listEmail,
					Set<Permission> permissions,
					boolean subscribed,
					String deliverTo)
	{
		this.listId = listId;
		this.listName = listName;
		this.listEmail = listEmail;
		this.perms = permissions;
		this.subscribed = subscribed;
		this.deliverTo = deliverTo;
	}

	/** */
	public String toString()
	{
		return this.getClass().getName() + " {list=" + this.listName + "}";
	}

	/** */
	public String getListEmail()
	{
		return this.listEmail;
	}

	/** */
	public Long getListId()
	{
		return this.listId;
	}

	/** */
	public String getListName()
	{
		return this.listName;
	}

	/** 
	 * @return the actual permissions of this user on the list.
	 * Note that list owners and site admins have all permissions.
	 */
	public Set<Permission> getPerms()
	{
		return this.perms;
	}

	/** */
	public String getDeliverTo()
	{
		return this.deliverTo;
	}

	/** */
	public boolean isSubscribed()
	{
		return this.subscribed;
	}
}

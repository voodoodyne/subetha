/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * All the possible permissions on a mailing list.
 * 
 * @author Jeff Schnitzer
 */
public enum Permission
{
	EDIT_SETTINGS,
	EDIT_ROLES,
	EDIT_FILTERS,
	APPROVE_MESSAGES,
	APPROVE_SUBSCRIPTIONS,
	POST,
	VIEW_SUBSCRIBERS,
	READ_ARCHIVES,
	READ_NOTES,
	EDIT_NOTES,
	VIEW_ADDRESSES;		// See email addresses in archives and subscription list
	
	/** A set that contains all permissions */
	public static final Set<Permission> ALL;
	static
	{
		Set<Permission> tmp = new TreeSet<Permission>();

		for (Permission p: Permission.values())
			tmp.add(p);

		ALL = Collections.unmodifiableSet(tmp);
	}
	
	/** */
	private String pretty;
	
	/** 
	 * Makes the pretty form which is mixed case and converts
	 * underscores to spaces.
	 */
	private Permission()
	{
		String original = this.toString();
		StringBuffer buf = new StringBuffer(original.length());
		
		boolean lowerNext = false;
		
		for (int i=0; i<original.length(); i++)
		{
			char ch = original.charAt(i);

			if (ch == '_')
			{
				lowerNext = false;
				buf.append(' ');
			}
			else if (lowerNext)
			{
				buf.append(Character.toLowerCase(ch));
			}
			else
			{
				buf.append(ch);
				lowerNext = true;
			}
		}
		
		this.pretty = buf.toString();
	}
	
	/** */
	public String getPretty()
	{
		return this.pretty;
	}
	
	public String getDescription(Permission val)
	{
		String response;
		switch (val)
		{
			case EDIT_SETTINGS:
				response = "Member can edit mailing list settings.";
				break;
			case EDIT_ROLES:
				response = "Edit the roles for members.";
				break;
			case EDIT_FILTERS:
				response = "Edit the filters of a mailing list.";
				break;
			case APPROVE_MESSAGES:
				response = "Approve messages to a mailing list.";
				break;
			case APPROVE_SUBSCRIPTIONS:
				response = "Approve subscriptions to a mailing list.";
				break;
			case POST:
				response = "Post messages to a mailing list.";
				break;
			case VIEW_SUBSCRIBERS:
				response = "View subscrivers to a mailing list.";
				break;
			case READ_ARCHIVES:
				response = "Read the archives of a mailing list.";
				break;
			case READ_NOTES:
				response = "Read private notes about a member of a mailing list.";
				break;
			case EDIT_NOTES:
				response = "Edit private notes about a member of a mailing list.";
				break;
			case VIEW_ADDRESSES:
				response = "Can view the address of members of a mailing list.";
				break;
			default:
				response = "Invalid permission";
				break;
		}
		return response;
	}
}

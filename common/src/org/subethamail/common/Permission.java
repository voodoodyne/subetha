/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
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
	SEE_ADDRESSES;		// See email addresses in archives and subscription list
	
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
}

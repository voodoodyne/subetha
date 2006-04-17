/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
		Set<Permission> tmp = new HashSet<Permission>();
		
		for (Permission p: Permission.values())
			tmp.add(p);
		
		ALL = Collections.unmodifiableSet(tmp);
	}
}

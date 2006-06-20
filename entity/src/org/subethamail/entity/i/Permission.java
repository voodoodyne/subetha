/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity.i;

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
	EDIT_SETTINGS("Edit the name, description, and major configuration options of the list."),
	EDIT_ROLES("Edit and reassign roles and permissions.  This should not be granted to non-owners."),
	VIEW_ROLES("View roles of members on the list subscribers page."),
	EDIT_FILTERS("Add, remove, and change the properties of mail filters."),
	APPROVE_MESSAGES("Approve messages which have been held for administrative approval."),
	APPROVE_SUBSCRIPTIONS("Approve subscription requests."),
	MASS_SUBSCRIBE("Subscribe other people to the mailing list."),
	UNSUBSCRIBE_OTHERS("Unsubscribe other people from the mailing list."),
	POST("Post messages to a mailing list, either by emailing the list address or from the archives."),
	VIEW_SUBSCRIBERS("View the list of subscribers to the mailing list."),
	READ_ARCHIVES("Read and search the archives of the mailing list."),
	READ_NOTES("Read administrative notes that have been placed on subscribers."),
	EDIT_NOTES("Create and edit administrative notes for subscribers."),
	VIEW_ADDRESSES("See the real email addresses of members in the archives and subscriber list."),
	IMPORT_MESSAGES("Import messages for a list. This allow direct recording of messages into the archives with no delivery.");

	/** A set that contains all permissions */
	public static final Set<Permission> ALL;
	static
	{
		Set<Permission> tmp = new TreeSet<Permission>();

		for (Permission p : Permission.values())
			tmp.add(p);

		ALL = Collections.unmodifiableSet(tmp);
	}
	
	/** */
	private String pretty;
	private String description;
	
	/** 
	 * Makes the pretty form which is mixed case and converts
	 * underscores to spaces.
	 */
	private Permission(String description)
	{
		this.description = description;
		
		// Now set up the pretty
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
	
	public String getDescription()
	{
		return this.description;
	}
}

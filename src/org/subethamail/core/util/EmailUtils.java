/*
 * $Id: VERPAddress.java 907 2007-02-07 09:06:00Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/VERPAddress.java $
 */

package org.subethamail.core.util;



/**
 * Static methods to help with email address manipulation.
 * @author Jeff Schnitzer
 */
public class EmailUtils
{
	/**
	 * Normalize the domain-part to lowercase.  If email address is missing
	 * an '@' the email is returned as-is.
	 */
	public static String normalizeEmail(String email)
	{
		int atIndex = email.indexOf('@');
		if (atIndex < 0)
			return email;
		else
			return email.substring(0, atIndex) + email.substring(atIndex).toLowerCase();
	}
}
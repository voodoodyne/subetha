/*
 * $Id: Validator.java 1001 2009-03-19 02:38:47Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.entity.i;

import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 * For validating data at all tiers.  These constants also define
 * the length of columns in the database.
 *
 * @author Jeff Schnitzer
 */
@Log
public class Validator
{
	// Config
	public static final int MAX_CONFIG_ID = 80;
	public static final int MAX_CONFIG_VALUE = 4096;
	
	// Person
	public static final int MAX_PERSON_PASSWORD = 80;
	public static final int MIN_PERSON_PASSWORD = 3;
	public static final int MAX_PERSON_NAME = 80;
	
	// EmailAddress
	public static final int MAX_EMAIL_ADDRESS = 255;
	
	// MailingList
	public static final int MAX_LIST_EMAIL = 255;
	public static final int MAX_LIST_NAME = 255;
	public static final int MAX_LIST_URL = 255;
	public static final int MAX_LIST_DESCRIPTION = 4096;
	public static final int MAX_LIST_WELCOME_MESSAGE = 4096;
	
	// Role
	public static final int MAX_ROLE_NAME = 50;
	
	// EnabledFilter
	public static final int MAX_FILTER_CLASSNAME = 255;
	
	// FilterArgument
	public static final int MAX_FILTER_ARGUMENT_NAME = 255;
	public static final int MAX_FILTER_ARGUMENT_VALUE = 4096;
	
	// Subscription
	public static final int MAX_SUBSCRIPTION_NOTE = 4096;
	
	// Mail
	public static final int MAX_MAIL_CONTENT = 1024 * 1024 * 1024;	// 1G
	public static final int MAX_MAIL_MESSAGE_ID = 255;
	public static final int MAX_MAIL_SUBJECT = 4096;
	public static final int MAX_MAIL_SENDER = 4096;
	
	// Attachment
	public static final int MAX_ATTACHMENT_CONTENT_TYPE = 255;
	public static final int MAX_ATTACHMENT_CONTENT = 1024 * 1024 * 1024;	// 1G

	/** default constructor prevents util class from being created. */
	private Validator() {}

	/**
	 * Normalizes an email address to a canonical form - the domain
	 * name is lowercased but the user part is left case sensitive.
	 * It's just a good idea to always work with addresses this way.
	 */
	public static String normalizeEmail(String email)
	{
		int atIndex = email.indexOf('@');
		
		StringBuffer buf = new StringBuffer(email.length());
		buf.append(email, 0, atIndex + 1);
		
		for (int i=atIndex+1; i<email.length(); i++)
			buf.append(Character.toLowerCase(email.charAt(i)));
		
		return buf.toString();
	}

	/**
	 * This method does its best to identify invalid internet email addresses.
	 * It just checks syntax structure and can be useful to catch really
	 * blatant typos or garbage data.
	 *
	 * @return whether or not the specified email address is valid.
	 */
	public static boolean validEmail(String email)
	{
		if (email == null || email.length() == 0)
			return false;
		
		if (email.length() > MAX_EMAIL_ADDRESS)
		{
		    log.log(Level.FINE,"Email too long: {0}", email);
			return false;
		}
		
		int indexOfAt = email.indexOf('@');

		if (indexOfAt < 1)
		{ 
			// must have @ and must not be 1st char
		    log.log(Level.FINE,"@ is first char: {0}", email);
			return false;
		}

		String site = email.substring(indexOfAt + 1);

		if (site.indexOf('@') >= 0)
		{
		    log.log(Level.FINE,"@ missing: {0}", email);
			return false;
		}

		if (site.startsWith(".") || site.endsWith("."))
		{
		    log.log(Level.FINE,"cannot start or end with ''.'': {0}", email);
			return false;
		}

		// Make sure we don't have a one-letter TLD
		if (site.length() - 2 > 0 && site.charAt(site.length() - 2) == '.')
		{
		    log.log(Level.FINE,"TLD too short:{0}", email);
			return false;
		}
		
		return true;
	}
}

/*
 * $Id: Validator.java 105 2006-02-27 10:06:27Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.common.valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * For validating data at all tiers.  These constants also define
 * the length of columns in the database.
 *
 * @author Jeff Schnitzer
 */
public class Validator
{
	/** */
	private static Log log = LogFactory.getLog(Validator.class);

	// Config
	public static final int MAX_CONFIG_ID = 80;
	public static final int MAX_CONFIG_VALUE = 4096;
	
	// Person
	public static final int MAX_PERSON_PASSWORD = 80;
	public static final int MAX_PERSON_NAME = 80;
	
	// EmailAddress
	public static final int MAX_EMAIL_ADDRESS = 255;
	
	// MailingList
	public static final int MAX_LIST_ADDRESS = 255;
	public static final int MAX_LIST_URL = 255;
	
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
	public static final int MAX_MAIL_CONTENT = 1024 * 1024;	// 1M enough?
	public static final int MAX_MAIL_MESSAGE_ID = 255;
	public static final int MAX_MAIL_SUBJECT = 4096;
	public static final int MAX_MAIL_FROM = 4096;
	
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
		if (email == null)
			return false;
		
		if (email.length() > MAX_EMAIL_ADDRESS){
			if (log.isDebugEnabled()) log.debug("Email too long: " + email);
			return false;
		}
		
		int indexOfAt = email.indexOf('@');

		if (indexOfAt < 1){	// must have @ and must not be 1st char
			if (log.isDebugEnabled()) log.debug("@ is first char: " + email);
			return false;
		}

		String site = email.substring(indexOfAt + 1);

		if (site.indexOf('@') >= 0){
			if (log.isDebugEnabled()) log.debug("@ missing: " + email);
			return false;
		}

		if (site.startsWith(".") || site.endsWith(".")){
			if (log.isDebugEnabled()) log.debug("cannot start or end with '.': " + email);
			return false;
		}
		
		// Make sure we don't have a one-letter TLD
		if (site.charAt(site.length() - 2) == '.'){
			if (log.isDebugEnabled()) log.debug("TLD too short:" + email);
			return false;
		}
		
		return true;
	}
}



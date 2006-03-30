/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.acct.i;

import javax.ejb.Local;
import javax.mail.MessagingException;


/**
 * Tools which let a user manipulate their own account data.
 * To use this ejb you must be authenticated.  All methods 
 * are relative to the caller principal person.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface AccountMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/AccountMgr/local";
		
	/**
	 * @return many details about myself, including email addresses
	 */
	public Self getSelf();
	
	/**
	 * Updates password
	 * 
	 * @param oldPassword The current password
	 * @param newPassword The new password
	 * 
	 * @return false if the old password was incorrect, true if succeeded
	 */
	public boolean setPassword(String oldPassword, String newPassword);

	/**
	 * Sends an email request to the new email address that when clicked,
	 * will result in the email address being added to my list of addresses.
	 * If the new email address is already associated with a Person, the
	 * accounts will be merged.
	 * 
	 * @param newEmail must be a valid email address
	 */
	public void requestAddEmail(String newEmail) throws MessagingException;
	
	/**
	 * Actually sets a user's email address based on a token generated
	 * by requestSetEmail.  Also sends an email to the old email address
	 * notifying the user of the change.
	 * 
	 * @param token was generated with requestAddEmail
	 */
	public void addEmail(String token) throws BadTokenException;
	
}


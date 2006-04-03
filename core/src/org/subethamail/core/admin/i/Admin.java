/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.admin.i;

import java.net.URL;

import javax.ejb.Local;
import javax.mail.internet.InternetAddress;

import org.subethamail.common.NotFoundException;

/**
 * Administrative interface for managing the site.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface Admin
{
	/** */
	public static final String JNDI_NAME = "subetha/Admin/local";

	/**
	 * Creates a mailing list.  If any of the initial owner addresses
	 * have not been registered, accounts will be created without confirmation.
	 * 
	 * @param address contains both the email address and the short textual name of the list
	 * @param url is a valid list URL, including the /list/ portion.
	 * @param description is a long description of this list
	 * @param initialOwners is a list of email addresses.
	 * 
	 * @throws CreateMailingListException if the address or url are already in use.
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners) throws CreateMailingListException;
	
	/**
	 * Finds a person's id if the user exists, or creates a user account and
	 * returns the new person's id.  If the user already exists, any additional
	 * personal name information in the address is ignored.
	 * 
	 * If a user is created, the password will be random.
	 * 
	 * @param address contains both the email addy and name of the user.
	 * 
	 * @return the id of the person with that email, which may have just
	 *  now been created.
	 */
	public Long establishPerson(InternetAddress address);
	
	/**
	 * Just like the other version, but allows the password to be set.  If
	 * the user already exists, password is ignored.
	 * 
	 * @param password can be null to get a random password
	 * 
	 * @see Admin#establishPerson(InternetAddress)
	 */
	public Long establishPerson(InternetAddress address, String password);
	
	/**
	 * Sets whether or not the person is a site admin.
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException;
}

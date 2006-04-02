/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.admin.i;

import java.util.Collection;

import javax.ejb.Local;

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
	 * @param address is a valid email address
	 * @param url is a valid list URL, including the /list/ portion.
	 * @param initialOwners is a list of email addresses.
	 * 
	 * @throws CreateMailingListException if the address or url are already in use.
	 */
	public Long createMailingList(String address, String url, Collection<String> initialOwners) throws CreateMailingListException;
	
	/**
	 * Finds a person's id if the user exists, or creates a user account and
	 * returns the new person's id.  If the user already exists, the name
	 * parameter is ignored.
	 * 
	 * If a user is created, the password will be random.
	 * 
	 * @param email is the address of the person to look for (or create)
	 * @param name will be ignored if the person already exists
	 * 
	 * @return the id of the person with that email, which may have just
	 *  now been created.
	 */
	public Long establishPerson(String email, String name);
	
	/**
	 * Just like the other version, but allows the password to be set.  If
	 * the user already exists, password is ignored.
	 * 
	 * @param password can be null to get a random password
	 * 
	 * @see Admin#establishPerson(String, String)
	 */
	public Long establishPerson(String email, String name, String password);
	
	/**
	 * Sets whether or not the person is a site admin.
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException;
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin.i;

import java.net.URL;
import java.util.List;

import javax.ejb.Local;
import javax.mail.internet.InternetAddress;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.lists.i.ListData;

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
	 * Puts an arbitrary string in the server log, useful for clients (especially
	 * unit tests) to delineate method calls.
	 */
	public void log(String msg);
	
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
	 * returns the new person's id.  If the user already exists, the password
	 * and any additional personal name information in the address is ignored.
	 * 
	 * @param address contains both the email addy and name of the user.
	 * @param password can be null to get a random password
	 * 
	 * @return the id of the person with that email, which may have just
	 *  now been created.
	 */
	public Long establishPerson(InternetAddress address, String password);
	
	/**
	 * Subscribes an existing user to the list, or changes the delivery
	 * address of an existing subscription. 
	 * 
	 * @param email must be one of the current user's email addresses,
	 *  or null to subscribe delivery disabled.
	 *  
	 * @return either OK or HELD
	 *  
	 * @throws NotFoundException if the list id or person id is not valid.
	 */
	public SubscribeResult subscribe(Long listId, Long personId, String email) throws NotFoundException;

	/**
	 * Subscribes a potentially never-before-seen user to the list.
	 * 
	 * @param address can be an existing email address or a new one, in which
	 *  case a new person will be created.
	 *  
	 * @return either OK or HELD
	 *  
	 * @throws NotFoundException if the list id is not valid.
	 */
	public AuthSubscribeResult subscribe(Long listId, InternetAddress address) throws NotFoundException;

	/**
	 * Sets whether or not the person is a site admin.
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException;
	
	/**
	 * TODO:  this (and the UI) should probably be paginated.
	 * 
	 * @return some information about all the lists on the site.
	 */
	public List<ListData> getAllLists();

	/**
	 * Adds an email address to an existing account.  If the email address
	 * is already associated with another account, the other account will
	 * be merged into this one and then deleted.
	 */
	public void addEmail(Long personId, String email) throws NotFoundException;
	
	/**
	 * Merges one account into another.  At the end of this method, the "to"
	 * person will have all of the email addresses and subscriptions of the
	 * "from" person, and the "from" person will be deleted.
	 */
	public void merge(Long fromPersonId, Long toPersonId) throws NotFoundException;
	
	/**
	 * Checks to see if any messages from this person are held for self-moderation
	 * but shouldn't be.  This might be the case immediately after merging two
	 * accounts; messages from one might be held but the other account is valid.
	 * It might also be the case after adding a new email address.
	 */
	public void selfModerate(Long personId) throws NotFoundException;
}

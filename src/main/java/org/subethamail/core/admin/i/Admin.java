package org.subethamail.core.admin.i;

import java.net.URL;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.PersonData;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.ListDataPlus;
import org.subethamail.entity.i.PermissionException;

/**
 * Administrative interface for managing the site.
 * 
 * @author Jeff Schnitzer
 */
public interface Admin
{
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
	 * @throws DuplicateListDataException if the address or url are already in use.
	 * @throws InvalidListDataException if some of the list data can't be used.
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners) throws DuplicateListDataException, InvalidListDataException;
	
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
	 * @param ignoreHold will bypass moderation hold for the subscriber
	 *  
	 * @return either OK or HELD
	 *  
	 * @throws NotFoundException if the list id or person id is not valid.
	 */
	public SubscribeResult subscribe(Long listId, Long personId, String email, boolean ignoreHold) throws NotFoundException;

	/**
	 * Subscribes a potentially never-before-seen user to the list.
	 * @param address can be an existing email address or a new one, in which
	 *  case a new person will be created.
	 * @param ignoreHold will bypass moderation hold for the subscriber
	 * @param silent if set true will not send a welcome message to a new subscriber
	 * 
	 * @return either OK or HELD
	 *  
	 * @throws NotFoundException if the list id is not valid.
	 */
	public AuthSubscribeResult subscribeEmail(Long listId, InternetAddress address, boolean ignoreHold, boolean silent) throws NotFoundException;

	/**
	 * UnSubscribes a user from a list
	 * 
	 * @param listId a valid listID
	 * @param personId a valid personId
	 *  
	 * @throws NotFoundException if the list id or person id is not valid.
	 */
	public void unsubscribe(Long listId, Long personId) throws NotFoundException;

	/**
	 * Sets whether or not the person is a site admin.
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException;
	
	/**
	 * Controls whether a Person is a site admin or not.
	 * 
	 * @throws NotFoundException if nobody has that email address
	 */
	public void setSiteAdminByEmail(String email, boolean siteAdmin) throws NotFoundException;

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
	 * 
	 * @return the number of messages that were approved
	 * @throws InterruptedException 
	 */
	public int selfModerate(Long personId) throws NotFoundException, InterruptedException;

	/**
	 * Gets a list of site administrators. It's a special role.
	 * @return a list of PersonData with isSiteAdmin() == true
	 */
	public List<PersonData> getSiteAdmins();
	
	/**
	 * Sets list email address and URL.
	 */
	public void setListAddresses(Long listId, InternetAddress address, URL url) throws NotFoundException, DuplicateListDataException, InvalidListDataException;
	
	/**
	 * Gets a list of all the lists on the system.
	 */
	public List<ListData> getLists(int skip, int count);

	/**
	 * Gets a list of lists matching a String query
	 */
	public List<ListData> searchLists(String query, int skip, int count);

	/**
	 * Gets a list of all the lists on the system.
	 */
	public List<ListDataPlus> getListsPlus(int skip, int count) throws NotFoundException, PermissionException;

	/**
	 * Gets a list of lists matching a String query
	 */
	public List<ListDataPlus> searchListsPlus(String query, int skip, int count) throws NotFoundException, PermissionException;

	/**
	 * Get the total number of lists in the system.
	 */
	public int countLists();

	/**
	 * Get the total number of lists which match the query.
	 */
	public int countListsQuery(String query);
	
	/**
	 * @return some useful administrative information about the site
	 */
	public SiteStatus getSiteStatus();
	
	/**
	 * Gets the default site url.
	 */
	public URL getDefaultSiteUrl();

	/**
	 * Deletes a mailing list.  Must be called by siteAdmin.
	 * 
	 * @param password is the password of the caller, for validation purposes.
	 * @return true if the password was correct and the list was deleted, false if it was incorrect.
	 */
	public boolean deleteList(Long listId, String password) throws NotFoundException;

	/**
	 * Sets the name of a person.  This can only be called in two cases:
	 * <ol><li>By any site administrator</li>
	 * <li>By owners of lists the person is subscribed to, but only when the person name is empty</li></ol>
	 * 
	 * @param personId The person whose name to set
	 * @param name The name to set to
	 * @throws NotFoundException if the person does not exist
	 * @throws PermissionException if you aren't allowed to do this
	 */
	public void setPersonName(Long personId, String name) throws NotFoundException, PermissionException;
	
	/**
	 * Rebuild the fulltext search indexes from scratch.
	 * Probably very expensive.
	 */
	public void rebuildSearchIndexes();
}

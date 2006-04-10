/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.lists.i;

import java.net.URL;
import java.util.List;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.SubscribeResult;

/**
 * Tools for querying and modifying list configurations.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface ListMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/ListMgr/local";

	/**
	 * Finds the id for a particular list URL.
	 * 
	 * No access control.
	 */
	public Long lookup(URL url) throws NotFoundException;
	
	/**
	 * Gets some data about a mailing list.  Includes the subscriber
	 * status (including role and permissions) of the person calling
	 * this method.
	 * 
	 * If not authenticated, subscriber status will reflect that state.
	 * If authed as site admin, all permissions are granted no matter
	 * what the actual role.
	 * 
	 * No access control.
	 */
	public MySubscription getMySubscription(Long listId) throws NotFoundException;

	/**
	 * Anonymously requests to subscribe to a list.  This results in sending
	 * a token to the email address.  The token can be used to the other
	 * subscribeAnonymous() method to actually subscribe.
	 * 
	 * @param email must be a valid email address.  Can be the address
	 *  of an existing user.
	 * @param name is a name for the user.  This will be stored as the
	 *  user's name if the email address is for a new account.
	 *  
	 * @throws NotFoundException if the list id is not valid.
	 */
	public SubscribeResult subscribeAnonymous(Long listId, String email, String name) throws NotFoundException;
	
	/**
	 * Actually executes the request from the method of the same name. 
	 * 
	 * @param token must have been created by subscribeAnonymous().
	 *  
	 * @throws BadTokenException if something was wrong with the token.
	 * @throws NotFoundException if the embedded list id is invalid.  Possibly
	 *  the list was deleted while the token was in transit.
	 */
	public SubscribeResult subscribeAnonymous(String token) throws BadTokenException, NotFoundException;
	
	/**
	 * Subscribes an email address to the list, or changes the delivery
	 * address of an existing subscription.  User must be authenticated.
	 * 
	 * @param email must be one of the current user's email addresses,
	 *  or null to subscribe delivery disabled.
	 *  
	 * @throws NotFoundException if the list id is not valid.
	 */
	public SubscribeResult subscribeMe(Long listId, String email) throws NotFoundException;

	/**
	 * Retrieves all the subscribers for a MailingList
	 * 
	 * @throws NotFoundException if the list id is not valid.
	 */
	public List<SubscriberData> getSubscribers(Long listId) throws NotFoundException;
}

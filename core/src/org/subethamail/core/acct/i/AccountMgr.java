/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;


/**
 * Tools which let a user manipulate their own account data.
 * Unless otherwiste noted in the method, all methods require
 * authentication.  All methods are relative to the caller
 * principal person.
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
	 * Updates name
	 */
	public void setName(String newName);
	
	/**
	 * Updates password
	 * 
	 * @param newPassword The new password
	 */
	public void setPassword(String newPassword);

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
	public void addEmailRequest(String newEmail);
	
	/**
	 * Actually performs the action specified by the addEmailRequest token.
	 * This method has no access control and can be called by anyone.
	 * 
	 * @param token was generated with addEmailRequest
	 * 
	 * @return credentials which could be used to log in, if necessary.
	 * 
	 * @throws NotFoundException if the person id embedded in the token
	 *  no longer exists.
	 */
	public AuthCredentials addEmail(String token) throws BadTokenException, NotFoundException;
	
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
	public void subscribeAnonymousRequest(Long listId, String email, String name) throws NotFoundException;
	
	/**
	 * Actually executes the request from the method of the same name.
	 * No access control.
	 * 
	 * @param token must have been created by subscribeAnonymous().
	 *  
	 * @throws BadTokenException if something was wrong with the token.
	 * @throws NotFoundException if the embedded list id is invalid.  Possibly
	 *  the list was deleted while the token was in transit.
	 */
	public AuthSubscribeResult subscribeAnonymous(String token) throws BadTokenException, NotFoundException;
	
	/**
	 * Subscribes an email address to the list, or changes the delivery
	 * address of an existing subscription.  User must be authenticated.
	 * 
	 * @param listId the mailing list id
	 * @param email must be one of the current user's email addresses,
	 *  or null to subscribe delivery disabled.
	 *  
	 * @throws NotFoundException if the list id is not valid.
	 */
	public SubscribeResult subscribeMe(Long listId, String email) throws NotFoundException;

	/**
	 * UnSubscribes a person from a list.
	 * 
	 * @param listId the mailing list id
	 *  
	 * @throws NotFoundException if the list id or email is not valid.
	 */
	public void unSubscribeMe(Long listId) throws NotFoundException;

	/**
	 * Requests that the user's password be sent back to them in plaintext.
	 * No access control.
	 * 
	 * @throws NotFoundException if no account has that email address.
	 */
	public void forgotPassword(String email) throws NotFoundException;
}

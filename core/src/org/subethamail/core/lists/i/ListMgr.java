/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.mail.internet.InternetAddress;

import org.subethamail.common.NotFoundException;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;

/**
 * Tools for querying and modifying list configurations.  Most methods
 * require that the caller principal have certain permissions on the list,
 * either defined by their subscription role or the anonymous role (if
 * there is no caller principal).
 *
 * @author Jeff Schnitzer
 */
@Remote
public interface ListMgr
{
	/** */
	public static final String JNDI_NAME = "subetha/ListMgr/remote";

	/**
	 * Finds the id for a particular list URL.
	 * 
	 * No access control.
	 */
	public Long lookup(URL url) throws NotFoundException;
	
	/**
	 * Sets list name and description and whether or not subscriptions
	 * are held for approval.
	 * Requires Permission.EDIT_SETTINGS
	 */
	public void setList(Long listId, String name, String description, String welcomeMessage, boolean holdSubs) throws NotFoundException, PermissionException;
	
	/**
	 * Changes whether or not a list holds subscriptions
	 * Requires Permission.EDIT_SETTINGS
	 */
	public void setHoldSubscriptions(Long listId, boolean value) throws NotFoundException, PermissionException;
	
	/**
	 * Gets some basic information about a mailing list. 
	 * No permissions necessary.
	 */
	public ListData getList(Long listId) throws NotFoundException;

	/**
	 * Gets the basic info about a role. 
	 * Requires Permission.EDIT_ROLES
	 */
	public RoleData getRole(Long roleId) throws NotFoundException, PermissionException;

	/**
	 * Gets information about the roles associated with a list.
	 * Requires Permission.EDIT_ROLES
	 */
	public ListRoles getRoles(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Adds a new role to the list.
	 * 
	 * @return the id of the new role
	 * 
	 * Requires Permission.EDIT_ROLES
	 */
	public Long addRole(Long listId, String name, Set<Permission> perms) throws NotFoundException, PermissionException;

	/**
	 * Changes the properties of an existing role.
	 * 
	 * @return the id of the list that owns the role, very useful to have.
	 * 
	 * Requires Permission.EDIT_ROLES
	 */
	public Long setRole(Long roleId, String name, Set<Permission> perms) throws NotFoundException, PermissionException;

	/**
	 * Sets the default role for a list.
	 * Requires Permission.EDIT_ROLES
	 * 
	 * @param roleId must be a role belonging to the list.
	 */
	public void setDefaultRole(Long listId, Long roleId) throws NotFoundException, PermissionException;
	
	/**
	 * Sets the anonymous role for a list.
	 * Requires Permission.EDIT_ROLES
	 * 
	 * @param roleId must be a role belonging to the list.
	 */
	public void setAnonymousRole(Long listId, Long roleId) throws NotFoundException, PermissionException;

	/**
	 * Deletes a role, converting all participants in that role to the
	 * alternate.  The roles must both belong to the same list.
	 * 
	 * You cannot delete the Owner role.
	 * Requires Permission.EDIT_ROLES
	 * 
	 * @return the id of the list which owns the roles.
	 */
	public Long deleteRole(Long deleteRoleId, Long convertToRoleId) throws NotFoundException, PermissionException;
	
	/**
	 * Gets information about all the filters that might or might
	 * not be enabled on a list.
	 * 
	 * Requires Permission.EDIT_FILTERS
	 */
	public Filters getFilters(Long listId) throws NotFoundException, PermissionException;
	
	/**
	 * Gets data for a filter on a list.  If the filter has not already
	 * been enabled, the EnabledFilterData is populated with default values.
	 * 
	 * Requires Permission.EDIT_FILTERS
	 */
	public EnabledFilterData getFilter(Long listId, String className) throws NotFoundException, PermissionException;
	
	/**
	 * Enables a filter on a list, or changes the data associated with that filter.
	 * If a FilterParameter is missing from args, it will be assigned its default
	 * value.  If an unrecognized FilterParameter is found in args, it is silently
	 * ignored.
	 * 
	 * Requires Permission.EDIT_FILTERS
	 */
	public void setFilter(Long listId, String className, Map<String, Object> args) throws NotFoundException, PermissionException;
	
	/**
	 * Enables a filter on a list with default FilterParameters.
	 * 
	 * Requires Permission.EDIT_FILTERS
	 */
	public void setFilter(Long listId, String className) throws NotFoundException, PermissionException;

	/**
	 * Disables a filter on a list.  Fails silently if filter is not enabled on the list.
	 * All argument data is deleted.
	 * 
	 * Requires Permission.EDIT_FILTERS
	 */
	public void disableFilter(Long listId, String className) throws NotFoundException, PermissionException;
	
	/**
	 * Subscribes a mass of users to the list
	 * 
	 * @param how allows some control of how the subscription happens
	 * @param addresses are the addresses to subscribe
	 */
	public void massSubscribe(Long listId, MassSubscribeType how, InternetAddress[] addresses) throws NotFoundException, PermissionException;

	/**
	 * @return all the held subscriptions on the list.  Note the roleName is always null.
	 * 
	 * Requires Permission.APPROVE_SUBSCRIPTIONS
	 */
	public List<SubscriberData> getHeldSubscriptions(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Approves a subscription hold.  User is notified.
	 * 
	 * Requires Permission.APPROVE_SUBSCRIPTIONS
	 */
	public void approveHeldSubscription(Long listId, Long personId) throws NotFoundException, PermissionException;

	/**
	 * Discards a subscription hold.  User is not notified.
	 * 
	 * Requires Permission.APPROVE_SUBSCRIPTIONS
	 */
	public void discardHeldSubscription(Long listId, Long personId) throws NotFoundException, PermissionException;

	/**
	 * Gets all the held messages for a mailing list.
	 * 
	 * Requires Permission.APPROVE_MESSAGES
	 */
	public Collection<MailHold> getHeldMessages(Long listId, int skip, int count) throws NotFoundException, PermissionException;
	
	/**
	 * Approves a held message.
	 * @return the id of the list to which the msg was sent.
	 * Requires Permission.APPROVE_MESSAGES
	 */
	public Long approveHeldMessage(Long msgId) throws NotFoundException, PermissionException;

	/**
	 * Discards a held message.
	 * @return the id of the list to which the msg was sent.
	 * Requires Permission.APPROVE_MESSAGES
	 */
	public Long discardHeldMessage(Long msgId) throws NotFoundException, PermissionException;
	
	/**
	 * Approves a held message, and subscribe the email address.
	 * @return the id of the list to which the msg was sent.
	 * Requires Permission.APPROVE_SUBSCRIPTIONS and Permissions.APPROVE_MESSAGES
	 */
	public Long approveHeldMessageAndSubscribe(Long msgId) throws NotFoundException, PermissionException;

	/**
	 * UnSubscribes a person from a list.
	 * 
	 * @param listId the mailing list id
	 * @param personId the person id
	 *  
	 * @throws NotFoundException if the list id or email is not valid.
	 * @throws PermissionException needs Permission.EDIT_SUBSCRIPTIONS
	 */
	public void unsubscribe(Long listId, Long personId) throws NotFoundException, PermissionException;

	/**
	 * Sets the role for a person for a list.
	 * 
	 * @param listId the mailist list id
	 * @param personId the person id	
	 * @param roleId the role that person fulfills
	 * 
	 * @throws NotFoundException If the list, person or role is not found.
	 * @throws PermissionException Requires Permission.EDIT_ROLES
	 */
	public void setSubscriptionRole(Long listId, Long personId, Long roleId) throws NotFoundException, PermissionException;
	
	/**
	 * @param deliverTo must be one of the user's email addresses.
	 * 
	 * @throws NotFoundException if the list or person does not exist.
	 * @throws PermissionException if the caller does not have Permission.EDIT_SUBSCRIPTIONS.
	 */
	public void setSubscriptionDelivery(Long listId, Long personId, String deliverTo) throws NotFoundException, PermissionException;
	
	/**
	 * @param note must be within the allowable length.
	 * 
	 * @throws NotFoundException if the list or person does not exist.
	 * @throws PermissionException if the caller does not have Permission.EDIT_NOTES.
	 */
	public void setSubscriptionNote(Long listId, Long personId, String note) throws NotFoundException, PermissionException;
	
	/**
	 * Gets information about a particular subscriber.
	 * 
	 * @throws NotFoundException if the list or person does not exist, or if the person is not a
	 *  subscriber to the list.
	 * @throws PermissionException if the caller does not have Permission.VIEW_SUBSCRIBERS
	 */
	public SubscriberData getSubscription(Long listId, Long personId) throws NotFoundException, PermissionException;

	/**
	 * Retrieves all the subscribers for a MailingList
	 * Requires Permission.VIEW_SUBSCRIBERS.
	 * 
	 * @throws NotFoundException if the list id is not valid.
	 */
	public List<SubscriberData> getSubscribers(Long listId, int skip, int count) throws NotFoundException, PermissionException;

	/**
	 * Gets a list of Subscribers on a list that match a given String query.
	 * Requires Permission.VIEW_SUBSCRIBERS
	 */
	public List<SubscriberData> searchSubscribers(Long listId, String query, int skip, int count) throws NotFoundException, PermissionException;

	/**
	 * Gets the number of Subscribers on a list
	 * Requires Permission.VIEW_SUBSCRIBERS
	 */
	public int countSubscribers(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Gets the number of Subscribers on a list for a given query
	 * Requires Permission.VIEW_SUBSCRIBERS
	 */
	public int countSubscribers(Long listId, String query) throws NotFoundException, PermissionException;

	/**
	 * Gets the number of subscriptions on a list in a held state.
	 * Requires Permission.APPROVE_SUBSCRIPTIONS
	 */
	public int countHeldSubscriptions(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Gets the number of messages on a list in a held state.
	 * Requires Permission.APPROVE_MESSAGES
	 */
	public int countHeldMessages(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Checks whether the caller prinicipal has the specified permission
	 * on the specified list.  Does nothing else.
	 */
	public void checkPermission(Long listId, Permission perm) throws NotFoundException, PermissionException;
}

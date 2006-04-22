/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.lists.i;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;

/**
 * Tools for querying and modifying list configurations.  Most methods
 * require that the caller principal have certain permissions on the list,
 * either defined by their subscription role or the anonymous role (if
 * there is no caller principal).
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
	 * Retrieves all the subscribers for a MailingList
	 * Requires Permission.VIEW_SUBSCRIBERS.
	 * 
	 * @throws NotFoundException if the list id is not valid.
	 */
	public List<SubscriberData> getSubscribers(Long listId) throws NotFoundException, PermissionException;

	/**
	 * Sets list name and description.
	 * Requires Permission.EDIT_SETTINGS
	 */
	public void setListName(Long listId, String name, String description) throws NotFoundException, PermissionException;
	
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
}

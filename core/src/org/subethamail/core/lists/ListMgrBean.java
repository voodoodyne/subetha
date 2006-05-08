/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.core.lists.i.Filters;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.ListMgrRemote;
import org.subethamail.core.lists.i.ListRoles;
import org.subethamail.core.lists.i.PermissionException;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.FilterArgument;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;

/**
 * Implementation of the AccountMgr interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="ListMgr")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
public class ListMgrBean extends PersonalBean implements ListMgr, ListMgrRemote
{
	/** */
	private static Log log = LogFactory.getLog(ListMgrBean.class);
	
	/** */
	@EJB FilterRunner filterRunner;

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#lookup(java.net.URL)
	 */
	public Long lookup(URL url) throws NotFoundException
	{
		return this.dao.findMailingList(url).getId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getSubscribers(java.lang.Long)
	 */
	public List<SubscriberData> getSubscribers(Long listId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.VIEW_SUBSCRIBERS);

		return Transmute.subscribers(list.getSubscriptions());
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setListName(java.lang.Long, java.lang.String, java.lang.String)
	 */
	public void setListName(Long listId, String name, String description) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_SETTINGS);
		
		list.setName(name);
		list.setDescription(description);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getList(java.lang.Long)
	 */
	public ListData getList(Long listId) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		
		return Transmute.mailingList(list);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getRoles(java.lang.Long)
	 */
	public ListRoles getRoles(Long listId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_ROLES);
		
		return new ListRoles(
				listId,
				Transmute.role(list.getDefaultRole()),
				Transmute.role(list.getAnonymousRole()),
				Transmute.roles(list.getRoles()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#addRole(java.lang.Long, java.lang.String, java.util.Set)
	 */
	public Long addRole(Long listId, String name, Set<Permission> perms) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_ROLES);
		
		Role role = new Role(list, name, perms);
		this.dao.persist(role);
		
		list.getRoles().add(role);
		
		return role.getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setRole(java.lang.Long, java.lang.String, java.util.Set)
	 */
	public Long setRole(Long roleId, String name, Set<Permission> perms) throws NotFoundException, PermissionException
	{
		Role role = this.getRoleForEdit(roleId);
		
		if (role.isOwner())
			throw new IllegalArgumentException("You cannot change the Owner role");
		
		role.setName(name);
		role.getPermissions().clear();
		role.getPermissions().addAll(perms);
		
		return role.getList().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setDefaultRole(java.lang.Long, java.lang.Long)
	 */
	public void setDefaultRole(Long listId, Long roleId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_ROLES);
		Role role = this.dao.findRole(roleId);
		
		list.setDefaultRole(role);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setAnonymousRole(java.lang.Long, java.lang.Long)
	 */
	public void setAnonymousRole(Long listId, Long roleId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_ROLES);
		Role role = this.dao.findRole(roleId);
		
		list.setAnonymousRole(role);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getRole(java.lang.Long)
	 */
	public RoleData getRole(Long roleId) throws NotFoundException, PermissionException
	{
		Role role = this.getRoleForEdit(roleId);
		return Transmute.role(role);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#deleteRole(java.lang.Long, java.lang.Long)
	 */
	public Long deleteRole(Long deleteRoleId, Long convertToRoleId) throws NotFoundException, PermissionException
	{
		Role deleteRole = this.getRoleForEdit(deleteRoleId);
		Role convertRole = this.getRoleForEdit(convertToRoleId);
		
		if (deleteRole.getList() != convertRole.getList())
			throw new IllegalArgumentException("Roles are not from the same list");

		if (deleteRole.getList().getDefaultRole() == deleteRole)
			deleteRole.getList().setDefaultRole(convertRole);
		
		if (deleteRole.getList().getAnonymousRole() == deleteRole)
			deleteRole.getList().setAnonymousRole(convertRole);

		List<Subscription> subs = this.dao.findSubscriptionsByRole(deleteRole.getId());
		for (Subscription sub: subs)
			sub.setRole(convertRole);
		
		deleteRole.getList().getRoles().remove(deleteRole);
		this.dao.remove(deleteRole);
		
		return convertRole.getList().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getFilters(java.lang.Long)
	 */
	public Filters getFilters(Long listId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);
		
		Map<String, Filter> allFilters = this.filterRunner.getFilters();
		
		List<FilterData> available = new ArrayList<FilterData>(allFilters.size() - list.getEnabledFilters().size());
		List<EnabledFilterData> enabled = new ArrayList<EnabledFilterData>(list.getEnabledFilters().size());
		
		for (Filter filt: allFilters.values())
		{
			EnabledFilter enabledFilt = list.getEnabledFilters().get(filt.getClass().getName());
			if (enabledFilt != null)
				enabled.add(Transmute.enabledFilter(filt, enabledFilt));
			else
				available.add(Transmute.filter(filt));
		}
		
		return new Filters(available, enabled);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getFilter(java.lang.Long, java.lang.String)
	 */
	public EnabledFilterData getFilter(Long listId, String className) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);
		
		Filter filt = this.filterRunner.getFilters().get(className);
		EnabledFilter enabled = list.getEnabledFilters().get(className);
		
		if (enabled != null)
		{
			return Transmute.enabledFilter(filt, enabled);
		}
		else
		{
			// Create what looks like an enabled filter but populated with defaults
			Map<String, Object> args = new HashMap<String, Object>();
			
			for (FilterParameter param: filt.getParameters())
				args.put(param.getName(), param.getDefaultValue());
			
			return new EnabledFilterData(
					className, filt.getName(), filt.getDescription(), filt.getParameters(),
					listId, args);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setFilter(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void setFilter(Long listId, String className, Map<String, Object> args) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);
		
		Filter filt = this.filterRunner.getFilters().get(className);
		
		EnabledFilter enabled = list.getEnabledFilters().get(className);
		if (enabled == null)
		{
			// Create it from scratch
			enabled = new EnabledFilter(list, className);
			this.dao.persist(enabled);
			list.addEnabledFilter(enabled);
			
			for (FilterParameter param: filt.getParameters())
			{
				Object value = args.get(param.getName());
				if (value == null)
					value = param.getDefaultValue();
				
				if (!param.getType().equals(value.getClass()))
					throw new IllegalArgumentException("Param " + param.getName() + " has " + value.getClass() + " but should have " + param.getType());
					
				FilterArgument farg = new FilterArgument(enabled, param.getName(), value);
				this.dao.persist(farg);
				enabled.addArgument(farg);
			}
		}
		else
		{
			// We need to synchronize the args to the enabled list.
			
			// First get rid of anything already enabled but not in the paramNames
			// We'll need a convenient set of filter params
			Set<String> paramNames = new HashSet<String>();
			for (FilterParameter param: filt.getParameters())
				paramNames.add(param.getName());
			
			Iterator<FilterArgument> enabledArgsIt = enabled.getArguments().values().iterator();
			while (enabledArgsIt.hasNext())
			{
				FilterArgument arg = enabledArgsIt.next();
				if (!paramNames.contains(arg.getName()))
				{
					this.dao.remove(arg);
					enabledArgsIt.remove();
				}
			}
			
			// Now add back in everything that is in the official list
			for (FilterParameter param: filt.getParameters())
			{
				Object value = args.get(param.getName());
				if (value == null)
					value = param.getDefaultValue();
				
				if (!param.getType().equals(value.getClass()))
					throw new IllegalArgumentException("Param " + param.getName() + " has class " + value.getClass() + " but should have class " + param.getType());
					
				FilterArgument farg = enabled.getArguments().get(param.getName());
				if (farg == null)
				{
					farg = new FilterArgument(enabled, param.getName(), value);
					this.dao.persist(farg);
					enabled.addArgument(farg);
				}
				else
				{
					farg.setValue(value);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#disableFilter(java.lang.Long, java.lang.String)
	 */
	public void disableFilter(Long listId, String className) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);
		
		EnabledFilter filt = list.getEnabledFilters().remove(className);
		if (filt == null)
		{
			if (log.isWarnEnabled())
				log.warn("Attempt to remove filter " + className + " which was not enabled on list " + list.getName());
		}
		else
		{
			this.dao.remove(filt);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getHeldSubscriptions(java.lang.Long)
	 */
	public List<SubscriberData> getHeldSubscriptions(Long listId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.APPROVE_SUBSCRIPTIONS);
		
		return Transmute.heldSubscriptions(list.getSubscriptionHolds());
	}
}

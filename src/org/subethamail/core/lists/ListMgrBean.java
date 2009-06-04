/*
 * $Id: ListMgrBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/ListMgrBean.java $
 */

package org.subethamail.core.lists;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Current;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.filter.FilterRunner;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.core.lists.i.Filters;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.ListRoles;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.core.lists.i.MassSubscribeType;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.FilterRegistry;
import org.subethamail.core.queue.InjectedQueueItem;
import org.subethamail.core.util.InjectBeanHelper;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.FilterArgument;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;

import com.caucho.config.Name;

/**
 * Implementation of the ListMgr interface.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Stateless(name="ListMgr")
@PermitAll
@RunAs(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ListMgrBean extends PersonalBean implements ListMgr
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ListMgrBean.class);

	/** */
	@Current FilterRunner filterRunner;
	@Current FilterRegistry filterReg;
	@Current Admin admin;
	@Current AccountMgr accountMgr;

	@SuppressWarnings("unchecked")
//	@InjectQueue
	@Name("inject")
	BlockingQueue q;	
	
	//@Current 
	InjectBeanHelper<Filter> fHelper = new InjectBeanHelper<Filter>();
	

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#lookup(java.net.URL)
	 */
	public Long lookup(URL url) throws NotFoundException
	{
		// Sometimes people looking for a list like "http://www.example.com/se/list"
		// might type in "http://www.example.com/se/list/", so we will check for
		// this.  However, keep in mind that the trailing / can be deliberate.
		//
		// They might also type in "http://example.com/se/list", so we should
		// try adding the www too.

		String stringified = url.toString();
		try
		{
			return this.em.getMailingList(url).getId();
		}
		catch (NotFoundException ex)
		{
			if (stringified.endsWith("/"))
			{
				try
				{
					url = new URL(stringified.substring(0, stringified.length()-1));
					return this.em.getMailingList(url).getId();
				}
				catch (MalformedURLException mux) { throw new RuntimeException(mux); }
				catch (NotFoundException nfx) { throw ex; }	// throw the original anyways
			}
			else if (!url.getHost().startsWith("www."))
			{
				try
				{
					int pivot = stringified.indexOf("//") + 2;
					String firstPart = stringified.substring(0, pivot);
					String secondPart = stringified.substring(pivot);
					url = new URL(firstPart + "www." + secondPart);

					return this.em.getMailingList(url).getId();
				}
				catch (MalformedURLException mux) { throw new RuntimeException(mux); }
				catch (NotFoundException nfx) { throw ex; }	// throw the original anyways
			}
			else
			{
				throw ex;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setList(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void setList(Long listId, String name, String description, String welcomeMessage, boolean holdSubs) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_SETTINGS);

		list.setName(name);
		list.setDescription(description);
		list.setWelcomeMessage(welcomeMessage);

		this.setHoldSubscriptions(list, holdSubs);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setHoldSubscriptions(java.lang.Long, boolean)
	 */
	public void setHoldSubscriptions(Long listId, boolean value) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_SETTINGS);

		this.setHoldSubscriptions(list, value);
	}

	/**
	 * Convenience method.  Consider whether this method should
	 * flush existing subscription holds.
	 */
	private void setHoldSubscriptions(MailingList list, boolean value)
	{
		boolean flushHolds = list.isSubscriptionHeld() && !value;

		list.setSubscriptionHeld(value);

		if (flushHolds)
		{
			// Flush all subscription holds?  Maybe it's better just
			// to leave them in the queue for the administrator.
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getList(java.lang.Long)
	 */
	public ListData getList(Long listId) throws NotFoundException
	{
		MailingList list = this.em.get(MailingList.class, listId);

		return Transmute.mailingList(list);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getList(java.lang.String)
	 */
	public ListData getListByEmail(String email) throws NotFoundException
	{
		try
		{
			InternetAddress addy = new InternetAddress(email);
			MailingList list = this.em.getMailingList(addy);

			return Transmute.mailingList(list);
		}
		catch (AddressException ex) { throw new NotFoundException(ex); }
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
		this.em.persist(role);

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
		Role role = this.em.get(Role.class, roleId);

		list.setDefaultRole(role);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setAnonymousRole(java.lang.Long, java.lang.Long)
	 */
	public void setAnonymousRole(Long listId, Long roleId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_ROLES);
		Role role = this.em.get(Role.class, roleId);

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

		List<Subscription> subs = this.em.findSubscriptionsByRole(deleteRole.getId());
		for (Subscription sub: subs)
			sub.setRole(convertRole);

		deleteRole.getList().getRoles().remove(deleteRole);
		this.em.remove(deleteRole);

		return convertRole.getList().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getFilters(java.lang.Long)
	 */
	public Filters getFilters(Long listId) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);

		Collection<Class<? extends Filter>> allFilters = this.filterReg.getFilters();

		List<FilterData> available = new ArrayList<FilterData>(allFilters.size() - list.getEnabledFilters().size());
		List<EnabledFilterData> enabled = new ArrayList<EnabledFilterData>(list.getEnabledFilters().size());

		for (Class<? extends Filter> filterClass: allFilters)
		{
			Filter filt = fHelper.getInstance(filterClass);
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

        Filter filt = fHelper.getInstance(className);
        
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
	 * @see org.subethamail.core.lists.i.ListMgr#setFilter(java.lang.Long, java.lang.String)
	 */
	public void setFilterDefault(Long listId, String className) throws NotFoundException, PermissionException
	{
		this.setFilter(listId, className, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setFilter(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void setFilter(Long listId, String className, Map<String, Object> args) throws NotFoundException, PermissionException
	{
		MailingList list = this.getListFor(listId, Permission.EDIT_FILTERS);

		Filter filt = fHelper.getInstance(className);
		
		if (filt == null)
			throw new IllegalStateException("Filter does not exist or can't be created by inject manager");

		EnabledFilter enabled = list.getEnabledFilters().get(className);
		if (enabled == null)
		{
			// Create it from scratch
			enabled = new EnabledFilter(list, className);
			this.em.persist(enabled);
			list.addEnabledFilter(enabled);

			for (FilterParameter param: filt.getParameters())
			{
				Object value = null;
				if (args != null)
				{
					value = args.get(param.getName());
					if (value == null)
						value = param.getDefaultValue();
				}
				else
				{
					value = param.getDefaultValue();
				}
				if (!param.getType().equals(value.getClass()))
					throw new IllegalArgumentException("Param " + param.getName() + " has " + value.getClass() + " but should have " + param.getType());

				FilterArgument farg = new FilterArgument(enabled, param.getName(), value);
				this.em.persist(farg);
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
					this.em.remove(arg);
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
					this.em.persist(farg);
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

		EnabledFilter filt = list.getEnabledFilters().get(className);
		if (filt == null)
		{
			if (log.isWarnEnabled())
				log.warn("Attempt to remove filter " + className + " which was not enabled on list " + list.getName());
		}
		else
		{
			list.getEnabledFilters().remove(className);
			this.em.remove(filt);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#massSubscribe(java.lang.Long, org.subethamail.core.lists.i.MassSubscribeType, javax.mail.internet.InternetAddress[])
	 */
	public void massSubscribe(Long listId, MassSubscribeType how, InternetAddress[] addresses) throws NotFoundException, PermissionException
	{
		// We don't need the object, but we need to check permission
		this.getListFor(listId, Permission.MASS_SUBSCRIBE);

		if (MassSubscribeType.INVITE.equals(how))
		{
			for (InternetAddress addy: addresses)
				this.accountMgr.subscribeAnonymousRequest(listId, addy.getAddress(), addy.getPersonal());
		}
		else if (MassSubscribeType.WELCOME.equals(how))
		{
			for (InternetAddress addy: addresses)
				this.admin.subscribeEmail(listId, addy, true, false);
		}
		else if (MassSubscribeType.SILENT.equals(how))
		{
			for (InternetAddress addy: addresses)
				this.admin.subscribeEmail(listId, addy, true, true);
		}
		else
		{
			throw new UnsupportedOperationException("Case not handled");
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

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#approveHeldSubscription(java.lang.Long, java.lang.Long)
	 */
	public void approveHeldSubscription(Long listId, Long personId) throws NotFoundException, PermissionException
	{
		SubscriptionHold discarded = this.discardHeldSubcriptionInternal(listId, personId);

		if (discarded != null)
		{
			String deliverTo = discarded.getDeliverTo() == null ? null : discarded.getDeliverTo().getId();
			this.admin.subscribe(listId, personId, deliverTo, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#discardHeldSubscription(java.lang.Long, java.lang.Long)
	 */
	public void discardHeldSubscription(Long listId, Long personId) throws NotFoundException, PermissionException
	{
		this.discardHeldSubcriptionInternal(listId, personId);
	}

	/**
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#approveHeldMessageAndSubscribe(java.lang.Long)
	 */
	public Long approveHeldMessageAndSubscribe(Long msgId) throws NotFoundException, PermissionException
	{
		// This is really just a subscribe operation, the message (and any
		// others with the same email address) will automatically get flushed.

		Mail mail = this.getMailFor(msgId, Permission.APPROVE_MESSAGES);
		mail.getList().checkPermission(this.getMe(), Permission.APPROVE_SUBSCRIPTIONS);

		this.admin.subscribeEmail(mail.getList().getId(), mail.getFromAddress(), true, false);

		return mail.getList().getId();
	}

	/**
	 * Convenient method discards the hold and returns the detached instance.
	 *
	 * @return null if no hold was found
	 */
	protected SubscriptionHold discardHeldSubcriptionInternal(Long listId, Long personId) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.APPROVE_SUBSCRIPTIONS);

		Person pers = this.em.get(Person.class, personId);

		SubscriptionHold hold = pers.getHeldSubscriptions().get(listId);
		if (hold != null)
		{
			pers.getHeldSubscriptions().remove(listId);
			this.em.remove(hold);
		}

		return hold;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#unsubscribe(java.lang.Long, java.lang.Long)
	 */
	public void unsubscribe(Long listId, Long personId) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.EDIT_SUBSCRIPTIONS);
		this.admin.unsubscribe(listId, personId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setSubscriptionRole(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public void setSubscriptionRole(Long listId, Long personId, Long roleId) throws NotFoundException, PermissionException
	{
		Subscription sub = this.getSubscriptionFor(listId, personId, Permission.EDIT_ROLES, this.getMe());

		sub.setRole(this.em.get(Role.class, roleId));
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getHeldMessages(java.lang.Long, int, int)
	 */
	public Collection<MailHold> getHeldMessages(Long listId, int skip, int count) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.APPROVE_MESSAGES);

		List<Mail> held = this.em.findMailHeld(listId, skip, count);

		return Transmute.heldMail(held);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#countHeldMessages(java.lang.Long)
	 */
	public int countHeldMessages(Long listId) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.APPROVE_MESSAGES);

		return this.em.countHeldMessages(listId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#approveHeldMessage(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public Long approveHeldMessage(Long msgId) throws NotFoundException, PermissionException, InterruptedException
	{
		Mail mail = this.getMailFor(msgId, Permission.APPROVE_MESSAGES);

		mail.approve();

		this.q.put(new InjectedQueueItem(mail));
//		this.queuer.queueForDelivery(mail.getId());

		return mail.getList().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#discardHeldMessage(java.lang.Long)
	 */
	public Long discardHeldMessage(Long msgId) throws NotFoundException, PermissionException
	{
		Mail mail = this.getMailFor(msgId, Permission.APPROVE_MESSAGES);

		this.em.remove(mail);

		return mail.getList().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#countHeldSubscriptions(java.lang.Long)
	 */
	public int countHeldSubscriptions(Long listId) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.APPROVE_SUBSCRIPTIONS);

		return this.em.countHeldSubscriptions(listId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getSubscribers(java.lang.Long, int, int)
	 */
	public List<SubscriberData> getSubscribers(Long listId, int skip, int count) throws NotFoundException, PermissionException
	{
		Person me = this.getMe();

		MailingList list = this.getListFor(listId, Permission.VIEW_SUBSCRIBERS, me);

		boolean showNotes = list.getPermissionsFor(me).contains(Permission.VIEW_NOTES);

		return Transmute.subscribers(this.em.findSubscribers(listId, skip, count), showNotes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#searchSubscribers(java.lang.Long, java.lang.String, int, int)
	 */
	public List<SubscriberData> searchSubscribers(Long listId, String query, int skip, int count) throws NotFoundException, PermissionException
	{
		Person me = this.getMe();

		MailingList list = this.getListFor(listId, Permission.VIEW_SUBSCRIBERS, me);

		boolean showNotes = list.getPermissionsFor(me).contains(Permission.VIEW_NOTES);

		return Transmute.subscribers(this.em.findSubscribers(listId, query, skip, count), showNotes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#countSubscribers(java.lang.Long)
	 */
	public int countSubscribers(Long listId) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.VIEW_SUBSCRIBERS);

		return this.em.countSubscribers(listId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#countSubscribers(java.lang.Long, java.lang.String)
	 */
	public int countSubscribersQuery(Long listId, String query) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, Permission.VIEW_SUBSCRIBERS);

		return this.em.countSubscribers(listId, query);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#checkPermission(java.lang.Long, org.subethamail.common.Permission)
	 */
	public void checkPermission(Long listId, Permission perm) throws NotFoundException, PermissionException
	{
		this.getListFor(listId, perm);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setSubscriptionDelivery(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public void setSubscriptionDelivery(Long listId, Long personId, String deliverTo) throws NotFoundException, PermissionException
	{
		Subscription sub = this.getSubscriptionFor(listId, personId, Permission.EDIT_SUBSCRIPTIONS, this.getMe());

		if (deliverTo == null)
		{
			sub.setDeliverTo(null);
		}
		else
		{
			EmailAddress addy = sub.getPerson().getEmailAddress(deliverTo);
			if (addy == null)
				throw new NotFoundException("Email address does not belong to the person");

			sub.setDeliverTo(addy);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#setSubscriptionNote(java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public void setSubscriptionNote(Long listId, Long personId, String note) throws NotFoundException, PermissionException
	{
		Subscription sub = this.getSubscriptionFor(listId, personId, Permission.EDIT_NOTES, this.getMe());

		sub.setNote(note.trim());
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.lists.i.ListMgr#getSubscription(java.lang.Long, java.lang.Long)
	 */
	public SubscriberData getSubscription(Long listId, Long personId) throws NotFoundException, PermissionException
	{
		Person me = this.getMe();

		Subscription sub = this.getSubscriptionFor(listId, personId, Permission.VIEW_SUBSCRIBERS, me);

		boolean showNote = sub.getList().getPermissionsFor(me).contains(Permission.VIEW_NOTES);

		return Transmute.subscriber(sub, showNote);
	}
/*
	public List<String> searchEmailAddresses(Long listId, String partialEmail)
	{
		//check for permissions.
		if (!this.getMe().isSiteAdmin())
		{
			Subscription s = this.getMe().getSubscription(listId);
			if(s == null || !s.getRole().getPermissions().contains(Permission.VIEW_ADDRESSES)) return null;
		}
		
		List<EmailAddress> r = this.em.searchEmailAddresses(listId, partialEmail);
		ArrayList<String> emails = new ArrayList<String>(r.size());
		for (EmailAddress email : r)
		{
			emails.add(email.getId());
		}
		return emails;	
	}
*/
	/*
	 * (non-Javadoc)
	 * @see ListMgr#getActionableSubscriptions(Long)
	 */
/*	public List<SubscriberData> getActionableSubscriptions(Long listId)
			throws NotFoundException, PermissionException
	{
		return Transmute.subscribers(this.em.findSubscriptionsWithActionNote(listId) , true);
	}
*/
}
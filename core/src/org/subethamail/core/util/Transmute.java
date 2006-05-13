/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Permission;
import org.subethamail.core.acct.i.MySubscription;
import org.subethamail.core.acct.i.SubscribedList;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;
import org.subethamail.entity.Mail.HoldType;



/**
 * Offers static utility methods to convert between internal entity
 * objects and public interface value objects. 
 * 
 * @author Jeff Schnitzer
 */
public class Transmute
{
	/** */
	private static Log log = LogFactory.getLog(Transmute.class);
	
	/** */
	public static List<BlueprintData> blueprints(Collection<Blueprint> rawColl)
	{
		List<BlueprintData> result = new ArrayList<BlueprintData>(rawColl.size());
		
		for (Blueprint raw: rawColl)
			result.add(blueprint(raw));
		
		return result;
	}
	
	/** */
	public static BlueprintData blueprint(Blueprint raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
			
		return new BlueprintData(
				raw.getClass().getName(),
				raw.getName(),
				raw.getDescription());
	}
	
	/** */
	public static List<ListData> mailingLists(Collection<MailingList> rawColl)
	{
		List<ListData> result = new ArrayList<ListData>(rawColl.size());
		
		for (MailingList raw: rawColl)
			result.add(mailingList(raw));
		
		return result;
	}
	
	/** */
	public static ListData mailingList(MailingList raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new ListData(
				raw.getId(),
				raw.getEmail(),
				raw.getName(),
				raw.getUrl(),
				raw.getDescription(),
				raw.isSubscriptionHeld());
	}

	/** Does not add held subscriptions */
	public static List<SubscriberData> subscribers(Collection<Subscription> subscriptions)
	{
		List<SubscriberData> result = new ArrayList<SubscriberData>(subscriptions.size());

		for (Subscription subscription: subscriptions)
			result.add(subscriber(subscription));
		
		return result;
	}
	
	/** */
	public static SubscriberData subscriber(Subscription raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());

		return new SubscriberData(
				raw.getPerson().getId(),
				raw.getPerson().getName(),
				raw.getPerson().getEmailArray(),
				raw.getRole().getName(),
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null,
				raw.getDateCreated());
	}

	/**
	 * @param rawPerson can be null
	 * @return the status of the person for the given list. 
	 */
	public static MySubscription mySubscription(Person rawPerson, MailingList rawList)
	{
		ListData listData = mailingList(rawList);
		
		Role role = rawList.getRoleFor(rawPerson);
		
		Set<Permission> perms = rawList.getPermissionsFor(rawPerson);

		perms.size();	// initialize if it's a proxy
		
		Subscription rawSub = (rawPerson == null) ? null : rawPerson.getSubscription(rawList.getId());
		EmailAddress deliverTo = (rawSub == null) ? null : rawSub.getDeliverTo();
		
		return new MySubscription(
				listData,
				(deliverTo == null) ? null : deliverTo.getId(),
				(rawSub != null),
				role.isOwner(),
				role.getName(),
				perms);
	}
	
	/**
	 * Just hides the exception handling
	 */
	public static InternetAddress internetAddress(String email, String name)
	{
		try
		{
			return new InternetAddress(email, name);
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new IllegalArgumentException("Unable to handle " + email + "/" + name, ex);
		}
	}

	public static List<SubscribedList> subscriptions(Collection<Subscription> rawColl)
	{
		List<SubscribedList> result = new ArrayList<SubscribedList>(rawColl.size());
		
		for (Subscription raw: rawColl)
			result.add(subscription(raw));
		
		return result;
	}

	/**
	 */
	public static SubscribedList subscription(Subscription raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new SubscribedList(
				raw.getList().getId(),
				raw.getList().getEmail(),
				raw.getList().getName(),
				raw.getList().getUrl(),
				raw.getList().getDescription(),
				raw.getList().isSubscriptionHeld(),
				raw.getRole().getName(),
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null);
	}

	/**
	 * @param showEmail determines whether or not the summary will contain the
	 *  email address of the author.
	 */
	public static List<MailSummary> mailSummaries(Collection<Mail> rawColl, boolean showEmail, MailSummary replacement)
	{
		List<MailSummary> result = new ArrayList<MailSummary>(rawColl.size());
		
		for (Mail raw: rawColl)
			result.add(mailSummary(raw, showEmail, replacement));
		
		return result;
	}

	/**
	 * @param replacement is a summary we stop and use instead of continuing recursion.
	 *  If null, no replacement will occur.
	 */
	public static MailSummary mailSummary(Mail raw, boolean showEmail, MailSummary replacement)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		if (replacement != null && replacement.getId().equals(raw.getId()))
			return replacement;
			
		InternetAddress addy = raw.getFromAddress();
			
		return new MailSummary(
				raw.getId(),
				raw.getSubject(),
				showEmail ? addy.getAddress() : null,
				addy.getPersonal(),
				raw.getDateCreated(),
				mailSummaries(raw.getReplies(), showEmail, replacement));
	}

	/**
	 */
	public static List<RoleData> roles(Collection<Role> rawColl)
	{
		List<RoleData> result = new ArrayList<RoleData>(rawColl.size());
		
		for (Role raw: rawColl)
			result.add(role(raw));
		
		return result;
	}

	/**
	 */
	public static RoleData role(Role raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		// Might be a proxy, must initialize 
		raw.getPermissions().size();
		
		return new RoleData(
				raw.getId(),
				raw.getName(),
				raw.isOwner(),
				raw.getPermissions(),
				raw.getList().getId());
	}

	/**
	 */
	public static List<FilterData> filters(Collection<Filter> rawColl)
	{
		List<FilterData> result = new ArrayList<FilterData>(rawColl.size());
		
		for (Filter raw: rawColl)
			result.add(filter(raw));
		
		return result;
	}

	/**
	 */
	public static FilterData filter(Filter raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new FilterData(
				raw.getClass().getName(),
				raw.getName(),
				raw.getDescription(),
				raw.getParameters());
	}

	/**
	 */
	public static EnabledFilterData enabledFilter(Filter filter, EnabledFilter enabled)
	{
		if (log.isDebugEnabled())
			log.debug(enabled.toString());
	
		return new EnabledFilterData(
				filter.getClass().getName(),
				filter.getName(),
				filter.getDescription(),
				filter.getParameters(),
				enabled.getList().getId(),
				enabled.getArgumentMap());
	}

	/**
	 */
	public static List<SubscriberData> heldSubscriptions(Collection<SubscriptionHold> rawColl)
	{
		List<SubscriberData> result = new ArrayList<SubscriberData>(rawColl.size());
		
		for (SubscriptionHold raw: rawColl)
			result.add(heldSubscription(raw));
		
		return result;
	}

	/**
	 */
	public static SubscriberData heldSubscription(SubscriptionHold raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new SubscriberData(
				raw.getPerson().getId(),
				raw.getPerson().getName(),
				raw.getPerson().getEmailArray(),
				null,
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null,
				raw.getDateCreated());
	}

	/**
	 */
	public static Collection<MailHold> heldMail(Collection<Mail> rawColl)
	{
		List<MailHold> result = new ArrayList<MailHold>(rawColl.size());
		
		for (Mail raw: rawColl)
			result.add(heldMail(raw));
		
		return result;
	}

	/**
	 */
	public static MailHold heldMail(Mail raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new MailHold(
				raw.getId(),
				raw.getSubject(),
				raw.getFrom(),
				raw.getDateCreated(),
				raw.getHold() == HoldType.HARD);
	}

}

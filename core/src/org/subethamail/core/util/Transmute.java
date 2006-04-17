/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Permission;
import org.subethamail.core.acct.i.MySubscription;
import org.subethamail.core.acct.i.SubscriptionData;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.lists.i.MailingListData;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;



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
	public static List<MailingListData> mailingLists(Collection<MailingList> rawColl)
	{
		List<MailingListData> result = new ArrayList<MailingListData>(rawColl.size());
		
		for (MailingList raw: rawColl)
			result.add(mailingList(raw));
		
		return result;
	}
	
	/** */
	public static MailingListData mailingList(MailingList raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new MailingListData(
				raw.getId(),
				raw.getEmail(),
				raw.getName(),
				raw.getUrl(),
				raw.getDescription());
	}

	/** */
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
				raw.getEmailAddresses(),
				raw.getRole().getName(),
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null);
	}

	/**
	 * @param rawPerson can be null
	 * @return the status of the person for the given list. 
	 */
	public static MySubscription mySubscription(Person rawPerson, MailingList rawList)
	{
		MailingListData listData = mailingList(rawList);
		
		Role role = rawList.getRoleFor(rawPerson);
		
		Set<Permission> perms = role.getPermissions();
		
		// If we're the site admin, override with all.
		if (rawPerson != null && rawPerson.isSiteAdmin())
			perms = Permission.ALL;
		
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

	public static List<SubscriptionData> subscriptions(Collection<Subscription> rawColl)
	{
		List<SubscriptionData> result = new ArrayList<SubscriptionData>(rawColl.size());
		
		for (Subscription raw: rawColl)
			result.add(subscription(raw));
		
		return result;
	}

	/**
	 */
	public static SubscriptionData subscription(Subscription raw)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		return new SubscriptionData(
				raw.getList().getId(),
				raw.getList().getEmail(),
				raw.getList().getName(),
				raw.getList().getUrl(),
				raw.getList().getDescription(),
				raw.getRole().getName(),
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null);
	}

	/**
	 * @param showEmail determines whether or not the summary will contain the
	 *  email address of the author.
	 */
	public static List<MailSummary> mailSummaries(Collection<Mail> rawColl, boolean showEmail)
	{
		List<MailSummary> result = new ArrayList<MailSummary>(rawColl.size());
		
		for (Mail raw: rawColl)
			result.add(mailSummary(raw, showEmail));
		
		return result;
	}

	/**
	 */
	public static MailSummary mailSummary(Mail raw, boolean showEmail)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());
	
		InternetAddress addy;
		try
		{
			addy = new InternetAddress(raw.getFromNormal());
		}
		catch (AddressException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
			
		return new MailSummary(
				raw.getId(),
				raw.getSubject(),
				showEmail ? addy.getAddress() : null,
				addy.getPersonal(),
				raw.getDateCreated(),
				mailSummaries(raw.getReplies(), showEmail));
	}
}

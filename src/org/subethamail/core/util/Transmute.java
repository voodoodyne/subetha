/*
 * $Id: Transmute.java 979 2008-10-08 01:14:25Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/Transmute.java $
 */

package org.subethamail.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.MailUtils;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.acct.i.MyListRelationship;
import org.subethamail.core.acct.i.PersonData;
import org.subethamail.core.acct.i.SubscribedList;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.lists.i.AttachmentPartData;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.core.lists.i.InlinePartData;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.ListDataPlus;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.lists.i.RoleData;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.lists.i.TextPartData;
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
import org.subethamail.entity.i.Permission;



/**
 * Offers static utility methods to convert between internal entity
 * objects and public interface value objects.
 *
 * @author Jeff Schnitzer
 * @author Jon Stevens
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
				raw.getUrlBase(),
				raw.getDescription(),
				raw.getWelcomeMessage(),
				raw.getOwnerEmail(),
				raw.isSubscriptionHeld());
	}

	/** */
	public static ListDataPlus mailingListPlus(MailingList raw, int subscriberCount, int messageCount)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());

		return new ListDataPlus(
				raw.getId(),
				raw.getEmail(),
				raw.getName(),
				raw.getUrl(),
				raw.getUrlBase(),
				raw.getDescription(),
				raw.getWelcomeMessage(),
				raw.getOwnerEmail(),
				raw.isSubscriptionHeld(),
				subscriberCount,
				messageCount);
	}

	/** Does not add held subscriptions */
	public static List<SubscriberData> subscribers(Collection<Subscription> subscriptions, boolean showNote)
	{
		List<SubscriberData> result = new ArrayList<SubscriberData>(subscriptions.size());

		for (Subscription subscription: subscriptions)
			result.add(subscriber(subscription, showNote));

		return result;
	}

	/** */
	public static SubscriberData subscriber(Subscription raw, boolean showNote)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());

		return new SubscriberData(
				raw.getPerson().getId(),
				raw.getPerson().getName(),
				raw.getPerson().getEmailList(),
				Transmute.role(raw.getRole()),
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null,
				raw.getDateCreated(),
				showNote ? raw.getNote() : null);
	}

	/**
	 * @param rawPerson can be null
	 * @return the status of the person for the given list.
	 */
	public static MyListRelationship myListRelationship(Person rawPerson, MailingList rawList)
	{
		Set<Permission> perms = rawList.getPermissionsFor(rawPerson);

		perms.size();	// initialize if it's a proxy

		Subscription rawSub = (rawPerson == null) ? null : rawPerson.getSubscription(rawList.getId());
		EmailAddress deliverTo = (rawSub == null) ? null : rawSub.getDeliverTo();

		return new MyListRelationship(
				rawList.getId(),
				rawList.getName(),
				rawList.getEmail(),
				perms,
				rawSub != null,
				deliverTo == null ? null : deliverTo.getId());
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

	/**
	 * @return a sorted list of subscriptions
	 */
	public static List<SubscribedList> subscriptions(Collection<Subscription> rawColl)
	{
		List<SubscribedList> result = new ArrayList<SubscribedList>(rawColl.size());

		for (Subscription raw: rawColl)
			result.add(subscription(raw));

		Collections.sort(result);

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
				raw.getList().getUrlBase(),
				raw.getList().getDescription(),
				raw.getList().getOwnerEmail(),
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
				raw.getList().getId(),
				raw.getSubject(),
				showEmail ? addy.getAddress() : null,
				addy.getPersonal(),
				raw.getSentDate(),
				mailSummaries(raw.getReplies(), showEmail, replacement));
	}

	public static MailData mailData(Mail raw, boolean showEmail)
	{
		try
		{
			InternetAddress addy = raw.getFromAddress();

			SubEthaMessage msg = new SubEthaMessage(null, raw.getContent());

			List<InlinePartData> inlineParts = new ArrayList<InlinePartData>();
			List<AttachmentPartData> attachmentParts = new ArrayList<AttachmentPartData>();

			for (Part part: msg.getParts())
			{
				String contentType = part.getContentType();
				if (contentType.startsWith(SubEthaMessage.DETACHMENT_MIME_TYPE))
				{
					//we need the orig Content-Type before the message was munged
					contentType = part.getHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE)[0];
					//put back the orig Content-Type
					part.setHeader(SubEthaMessage.HDR_CONTENT_TYPE, contentType);

					String name = part.getFileName();

					// just in case we are working with something that isn't
					// C-D: attachment; filename=""
					if (name == null || name.length() == 0)
						name = MailUtils.getNameFromContentType(contentType);

					Long id = (Long) part.getContent();

					//TODO: Set the correct size. This should be the size of the Attachment.content (Blob)
					AttachmentPartData apd = new AttachmentPartData(id, contentType, name, 0);
					attachmentParts.add(apd);
				}
				else
				{
					// not an attachment cause it isn't stored as a detached part.
					Object content = part.getContent();

					String name = part.getFileName();

					// just in case we are working with something that isn't
					// C-D: attachment; filename=""
					if (name == null || name.length() == 0)
						name = MailUtils.getNameFromContentType(contentType);

					InlinePartData ipd;
					if (content instanceof String)
					{
						ipd = new TextPartData((String)content, part.getContentType(), name, part.getSize());
					}
					else
					{
						ipd = new InlinePartData(content, part.getContentType(), name, part.getSize());
					}

					inlineParts.add(ipd);
				}
			}

			return new MailData(
					raw.getId(),
					raw.getSubject(),
					showEmail ? addy.getAddress() : null,
					addy.getPersonal(),
					raw.getSentDate(),
					Transmute.mailSummaries(raw.getReplies(), showEmail, null),
					raw.getList().getId(),
					inlineParts,
					attachmentParts);
		}
		catch (MessagingException ex)
		{
			// Should be impossible since everything was already run through
			// JavaMail when the data was imported.
			throw new RuntimeException(ex);
		}
		catch (IOException ex)
		{
			// Ditto
			throw new RuntimeException(ex);
		}
	}

	/**
	 */
	public static MailData[] mailThread(Mail raw, boolean showEmail)
	{
		if (log.isDebugEnabled())
			log.debug(raw.toString());

		Mail root = raw;
		while (root.getParent() != null) { root = root.getParent(); }

		List<Mail> mails = raw.getDescendents();
		MailData[] mailDatas =  new MailData[mails.size()];

		int x = 0;
		for (Mail mail: mails)
		{
			mailDatas[x] = Transmute.mailData(mail, showEmail);
		}

		return mailDatas;
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
				raw.getPerson().getEmailList(),
				null,
				(raw.getDeliverTo() != null) ? raw.getDeliverTo().getId() : null,
				raw.getDateCreated(),
				null);
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
				raw.getSentDate(),
				raw.getHold() == HoldType.HARD);
	}

	/**
	 * This method converts Person objects into PersonData objects.
	 */
	public static List<PersonData> people(Collection<Person> persons)
	{
		List<PersonData> result = new ArrayList<PersonData>(persons.size());
		for (Person person: persons)
		{
			result.add(person(person));
		}
		return result;
	}

	/**
	 * This method converts a Person object into a PersonData object.
	 */
	public static PersonData person(Person person)
	{
		return new PersonData(person.getId(),
				person.getName(),
				person.getEmailList(),
				person.isSiteAdmin());
	}
}

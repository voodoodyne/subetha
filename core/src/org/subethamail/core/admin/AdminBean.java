/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.admin;

import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;
import org.subethamail.core.admin.i.CreateMailingListException;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the Admin interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="Admin")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class AdminBean implements Admin, AdminRemote
{
	/** */
	private static Log log = LogFactory.getLog(AdminBean.class);
	
	/**
	 * The set of characters from which randomly generated
	 * passwords will be obtained.
	 */
	protected static final String PASSWORD_GEN_CHARS =
		"abcdefghijklmnopqrstuvwxyz" +
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"0123456789";
	
	/**
	 * The length of randomly generated passwords.
	 */
	protected static final int PASSWORD_GEN_LENGTH = 6;
	
	/** */
	@EJB DAO dao;
	@EJB PostOffice postOffice;

	/**
	 * For generating random passwords.
	 */
	protected Random randomizer = new Random();
	
	/**
	 * @see Admin#log(String)
	 */
	public void log(String msg)
	{
		if (log.isInfoEnabled())
			log.info("CLIENT:  " + msg);
	}
	
	/**
	 * @see Admin#createMailingList(InternetAddress, URL, String, InternetAddress[])
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners) throws CreateMailingListException
	{
		// TODO:  consider whether or not we should enforce any formatting of
		// the url here.  Seems like that's a job for the web front end?
		
		// Make sure address and url are not duplicates
		boolean dupAddress = false;
		boolean dupUrl = false;
		
		try
		{
			this.dao.findMailingList(address);
			dupAddress = true;
		}
		catch (NotFoundException ex) {}
		
		try
		{
			this.dao.findMailingList(url);
			dupUrl = true;
		}
		catch (NotFoundException ex) {}
		
		if (dupAddress || dupUrl)
			throw new CreateMailingListException("Mailing list already exists", dupAddress, dupUrl);
		
		// Then create the mailing list and attach the owners.
		MailingList list = new MailingList(address.getAddress(), address.getPersonal(), url.toString(), description);
		this.dao.persist(list);
		// TODO:  remove this code when http://opensource.atlassian.com/projects/hibernate/browse/HHH-1654
		// is fixed.  This should be performed within the constructor of MailingList.
		list.setDefaultRole(list.getRoles().iterator().next());
		list.setAnonymousRole(list.getRoles().iterator().next());
		
		for (InternetAddress ownerAddress: initialOwners)
		{
			EmailAddress ea = this.establishEmailAddress(ownerAddress, null);
			Subscription sub = new Subscription(ea.getPerson(), list, ea, list.getOwnerRole());
			
			this.dao.persist(sub);
			
			list.getSubscriptions().add(sub);
			ea.getPerson().addSubscription(sub);
			
			this.postOffice.sendOwnerNewMailingList(ea, list);
		}
		
		return list.getId();
	}

	/**
	 * @see Admin#establishPerson(InternetAddress, String)
	 */
	public Long establishPerson(InternetAddress address, String password)
	{
		return this.establishEmailAddress(address, password).getPerson().getId();
	}

	/**
	 * @see AdminInternal#establishEmailAddress(InternetAddress, String)
	 */
	public EmailAddress establishEmailAddress(InternetAddress address, String password)
	{
		try
		{
			return this.dao.findEmailAddress(address.getAddress());
		}
		catch (NotFoundException ex)
		{
			// Nobody with that name, lets create
			
			if (password == null)
				password = this.generateRandomPassword();
			
			String personal = address.getPersonal();
			if (personal == null)
				personal = "";
			
			Person p = new Person(password, personal);
			EmailAddress e = new EmailAddress(p, address.getAddress());
			p.addEmailAddress(e);
			
			this.dao.persist(p);
			this.dao.persist(e);
			
			return e;
		}
	}

	/**
	 * @see Admin#subscribe(Long, String, String)
	 */
	public AuthSubscribeResult subscribe(Long listId, InternetAddress address) throws NotFoundException
	{
		EmailAddress addy = this.establishEmailAddress(address, null);
		
		SubscribeResult result = this.subscribe(listId, addy.getPerson(), addy);
		
		return new AuthSubscribeResult(addy.getId(), addy.getPerson().getPassword(), result, listId);
	}
	
	/**
	 * @see Admin#subscribe(Long, Long, String)
	 */
	public SubscribeResult subscribe(Long listId, Long personId, String email) throws NotFoundException
	{
		Person who = this.dao.findPerson(personId);
		
		if (email == null)
		{
			// Subscribing with (or changing to) disabled delivery
			return this.subscribe(listId, who, null);
		}
		else
		{
			EmailAddress addy = who.getEmailAddress(email);
			
			if (addy == null)
				throw new IllegalStateException("Must be one of person's email addresses");
			
			return this.subscribe(listId, who, addy);
		}
	}
	
	/**
	 * Subscribes someone to a mailing list, or changes the delivery address
	 * of an existing subscriber.
	 * 
	 * @param deliverTo can be null to disable delivery
	 */
	protected SubscribeResult subscribe(Long listId, Person who, EmailAddress deliverTo) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		
		Subscription sub = who.getSubscription(listId);
		if (sub != null)
		{
			// If we're already subscribed, maybe we want to change the
			// delivery address.
			sub.setDeliverTo(deliverTo);
			
			return SubscribeResult.OK;
		}
		else
		{
			// TODO:  maybe we need a subscription hold?
			
			sub = new Subscription(who, list, deliverTo, list.getDefaultRole());
			this.dao.persist(sub);
			
			who.addSubscription(sub);
			list.getSubscriptions().add(sub);
			
			this.postOffice.sendSubscribed(list, who, deliverTo);
			
			return SubscribeResult.OK;
		}
	}

	/**
	 * @return a valid password.
	 */
	protected String generateRandomPassword()
	{
		StringBuffer gen = new StringBuffer(PASSWORD_GEN_LENGTH);
		
		for (int i=0; i<PASSWORD_GEN_LENGTH; i++)
		{
			int which = (int)(PASSWORD_GEN_CHARS.length() * randomizer.nextDouble());
			
			gen.append(PASSWORD_GEN_CHARS.charAt(which));
		}
		
		return gen.toString();
	}

	/**
	 * @see Admin#setSiteAdmin(Long, boolean)
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException
	{
		Person p = this.dao.findPerson(personId);
		p.setSiteAdmin(value);
	}

	/**
	 * @see Admin#getAllLists()
	 */
	public List<ListData> getAllLists()
	{
		log.debug("Getting data for all lists");
		return Transmute.mailingLists(this.dao.findAllLists());
	}

	/**
	 * @see Admin#addEmail(Long, String)
	 */
	public void addEmail(Long personId, String email) throws NotFoundException
	{
		EmailAddress addy = this.dao.getEmailAddress(email);
		
		// Three cases:  either addy is null, addy is already associated with
		// the person, or addy is already associated with someone else.
		
		// Lets quickly handle the case were we don't have to do anything
		if (addy != null && addy.getPerson().getId().equals(personId))
			return;
		
		Person who = this.dao.findPerson(personId);
			
		if (addy == null)
		{
			addy = new EmailAddress(who, email);
			this.dao.persist(addy);
			who.addEmailAddress(addy);
		}
		else
		{
			this.merge(addy.getPerson().getId(), who.getId());
		}
		
		this.selfModerate(who.getId());
	}

	/**
	 * @see Admin#merge(Long, Long)
	 */
	public void merge(Long fromPersonId, Long toPersonId) throws NotFoundException
	{
		Person from = this.dao.findPerson(fromPersonId);
		Person to = this.dao.findPerson(toPersonId);

		// Move email addresses
		for (EmailAddress addy: from.getEmailAddresses().values())
		{
			addy.setPerson(to);
			to.addEmailAddress(addy);
		}
		
		from.getEmailAddresses().clear();
		
		// Move subscriptions
		for (Subscription sub: from.getSubscriptions().values())
		{
			// Keep our current subscription if there is a duplicate
			if (!to.getSubscriptions().containsKey(sub.getList().getId()))
			{
				sub.setPerson(to);
				to.addSubscription(sub);
			}
		}
		
		from.getSubscriptions().clear();
		
		// Nuke the old person object
		this.dao.remove(from);
	}

	/**
	 * @see Admin#selfModerate(Long)
	 */
	public void selfModerate(Long personId) throws NotFoundException
	{
		//TODO
		
		// Use a query to join against the email address table and the
		// mail table to pull out mail objects with SELF holds.
	}
}

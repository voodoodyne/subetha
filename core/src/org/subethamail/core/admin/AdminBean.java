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
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;
import org.subethamail.core.admin.i.CreateMailingListException;
import org.subethamail.core.lists.i.MailingListData;
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
		
		for (InternetAddress ownerAddress: initialOwners)
		{
			EmailAddress ea = this.establishEmailAddress(ownerAddress, null);
			Subscription sub = new Subscription(ea.getPerson(), list, ea, null);
			
			this.dao.persist(sub);
			
			list.getSubscriptions().add(sub);
			ea.getPerson().addSubscription(sub);
			
			try
			{
				this.postOffice.sendOwnerNewMailingList(ea, list);
			}
			catch (MessagingException ex)
			{
				log.error("Unable to send list owner notification of new list", ex);
				// Lets propagate the exception and abort everything, this is serious.
				// At worst someone will get email about a new list that doesn't exist.
				// Most likely, if javamail will let us send one msg, it will let us
				// send all of them, so any problem will come from the first msg.
				throw new RuntimeException(ex);
			}
		}
		
		return list.getId();
	}

	/**
	 * @see Admin#establishPerson(InternetAddress)
	 */
	public Long establishPerson(InternetAddress address)
	{
		return this.establishPerson(address, null);
	}

	/**
	 * @see Admin#establishPerson(InternetAddress, String)
	 */
	public Long establishPerson(InternetAddress address, String password)
	{
		return this.establishEmailAddress(address, password).getPerson().getId();
	}

	/**
	 * Establishes the email address and the person entity.
	 */
	protected EmailAddress establishEmailAddress(InternetAddress address, String password)
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
	public List<MailingListData> getAllLists()
	{
		log.debug("Getting data for all lists");
		return Transmute.mailingLists(this.dao.findAllLists());
	}
}

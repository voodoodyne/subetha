/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.admin;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;
import org.subethamail.core.admin.i.CreateMailingListException;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Person;
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

	/**
	 * For generating random passwords.
	 */
	protected Random randomizer = new Random();
	
	/**
	 * @see Admin#createMailingList(String, String, Collection)
	 */
	public Long createMailingList(String address, String url, Collection<InternetAddress> initialOwners) throws CreateMailingListException
	{
		//TODO
		
		// First make sure we have a safe address and url.  Check
		// for validity and duplicates.
		
		// Then ensure that all inital owners have accounts, and 
		// build a list of the associated Person objects
		
		// Then create the mailing list and attach the owners.
		
		return null;
	}

	/**
	 * @see Admin#establishPerson(String, String)
	 */
	public Long establishPerson(String email, String name)
	{
		return this.establishPerson(email, name, null);
	}

	/**
	 * @see Admin#establishPerson(String, String, String)
	 */
	public Long establishPerson(String email, String name, String password)
	{
		try
		{
			return this.dao.findEmailAddress(email).getPerson().getId();
		}
		catch (NotFoundException ex)
		{
			// Nobody with that name, lets create
			
			if (password == null)
				password = this.generateRandomPassword();
			
			Person p = new Person(password, name);
			EmailAddress e = new EmailAddress(p, email);
			p.setEmailAddresses(Collections.singleton(e));
			
			this.dao.persist(p);
			this.dao.persist(e);
			
			// TODO:  send the person an email about their new account
			
			return p.getId();
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
}

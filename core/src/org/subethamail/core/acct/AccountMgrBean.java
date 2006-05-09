/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.acct.i.AccountMgrRemote;
import org.subethamail.core.acct.i.AuthCredentials;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.MySubscription;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.Base62;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the AccountMgr interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="AccountMgr")
@SecurityDomain("subetha")
@RolesAllowed("user")
@RunAs("siteAdmin")
public class AccountMgrBean extends PersonalBean implements AccountMgr, AccountMgrRemote
{
	/** */
	private static Log log = LogFactory.getLog(AccountMgrBean.class);

	/**
	 * A known prefix so we know if decryption worked properly
	 */
	private static final String SUBSCRIBE_TOKEN_PREFIX = "sub";
	private static final String ADD_EMAIL_TOKEN_PREFIX = "add";
	
	/**
	 * Allow tokens to be at most 24 hours old
	 */
	public static final long MAX_TOKEN_AGE_MILLIS = 1000 * 60 * 60 * 24;
	
	/**
	 */
	@EJB DAO dao;
	@EJB PostOffice postOffice;
	@EJB Encryptor encryptor;
	@EJB Admin admin;
	
	/**
	 * @see AccountMgr#getSelf()
	 */
	public Self getSelf()
	{
		log.debug("Getting self");
		
		Person me = this.getMe();
		
		return new Self(
				me.getId(),
				me.getName(),
				me.getEmailArray(),
				me.isSiteAdmin(),
				Transmute.subscriptions(me.getSubscriptions().values())
			);
	}

	/**
	 * @see AccountMgr#setName(String)
	 */
	public void setName(String newName)
	{
		log.debug("Setting name");
		Person me = this.getMe();
		me.setName(newName);
	}

	/**
	 * @see AccountMgr#setPassword(String)
	 */
	public void setPassword(String newPassword)
	{	
		log.debug("Setting password");
		
		Person me = this.getMe();
		me.setPassword(newPassword);
	}

	/**
	 * @see AccountMgr#setPassword(String, String)
	 */
	public boolean setPassword(String oldPassword, String newPassword)
	{	
		log.debug("Setting password");
		
		Person me = this.getMe();
		
		// check the old password, current really.
		if (!me.checkPassword(oldPassword))
			return false;
		
		me.setPassword(newPassword);
		
		return true;
	}

	/**
	 * @see AccountMgr#addEmailRequest(String)
	 */
	public void addEmailRequest(String newEmail)
	{
		// Send a token to the person's account
		if (log.isDebugEnabled())
			log.debug("Requesting to add email " + newEmail);
		
		Person me = this.getMe();
		
		List<String> plainList = new ArrayList<String>();
		plainList.add(ADD_EMAIL_TOKEN_PREFIX);
		plainList.add(me.getId().toString());
		plainList.add(newEmail);
		
		byte[] cipherText = this.encryptor.encryptList(plainList);
		
		String cipherString = Base62.encode(cipherText);
		
		this.postOffice.sendAddEmailToken(me, newEmail, cipherString);
	}

	/**
	 * @see AccountMgr#addEmail(String)
	 */
	@PermitAll
	public AuthCredentials addEmail(String token) throws BadTokenException, NotFoundException
	{
		byte[] cipherText = Base62.decode(token);
		
		List<String> plainList;
		try
		{
			plainList = this.encryptor.decryptList(cipherText, MAX_TOKEN_AGE_MILLIS);
		}
		catch (GeneralSecurityException ex) { throw new BadTokenException(ex); }
		
		if (plainList.isEmpty() || !plainList.get(0).equals(ADD_EMAIL_TOKEN_PREFIX))
			throw new BadTokenException("Invalid token");
		
		Long personId = Long.valueOf(plainList.get(1));
		String email = plainList.get(2);


		this.admin.addEmail(personId, email);
		
		Person p = this.dao.findPerson(personId);
		
		return new AuthCredentials(email, p.getPassword());
	}
	
	/**
	 * @see AccountMgr#getMySubscription(Long)
	 */
	@PermitAll
	public MySubscription getMySubscription(Long listId) throws NotFoundException
	{
		MailingList ml = this.dao.findMailingList(listId);
		Person me = this.getMe();
			
		return Transmute.mySubscription(me, ml);
	}

	/**
	 * @see AccountMgr#subscribeAnonymousRequest(Long, String, String)
	 * 
	 * The token emailed is encrypted "listId:email:name".
	 */
	@PermitAll
	public void subscribeAnonymousRequest(Long listId, String email, String name) throws NotFoundException
	{
		// Send a token to the person's account
		if (log.isDebugEnabled())
			log.debug("Requesting to subscribe " + email + " to list " + listId);
		
		// A null name is not allowed, but empty is ok
		if (name == null)
			name = "";
		
		MailingList mailingList = this.dao.findMailingList(listId);
		
		List<String> plainList = new ArrayList<String>();
		plainList.add(SUBSCRIBE_TOKEN_PREFIX);
		plainList.add(listId.toString());
		plainList.add(email);
		plainList.add(name);
		
		byte[] cipherText = this.encryptor.encryptList(plainList);

		String cipherString = Base62.encode(cipherText);
		
		this.postOffice.sendConfirmSubscribeToken(mailingList, email, cipherString);
	}

	/**
	 * @see AccountMgr#subscribeAnonymous(String)
	 */
	@PermitAll
	public AuthSubscribeResult subscribeAnonymous(String token) throws BadTokenException, NotFoundException
	{
		byte[] cipherText = Base62.decode(token);
		
		List<String> plainList;
		try
		{
			plainList = this.encryptor.decryptList(cipherText, MAX_TOKEN_AGE_MILLIS);
		}
		catch (GeneralSecurityException ex) { throw new BadTokenException(ex); }
		
		if (plainList.isEmpty() || !plainList.get(0).equals(SUBSCRIBE_TOKEN_PREFIX))
			throw new BadTokenException("Invalid token");
		
		Long listId = Long.valueOf(plainList.get(1));
		String email = plainList.get(2);
		String name = plainList.get(3);

		InternetAddress address = Transmute.internetAddress(email, name);
		
		return this.admin.subscribe(listId, address, false);
	}

	/**
	 * @see AccountMgr#subscribeMe(Long, String)
	 */
	public SubscribeResult subscribeMe(Long listId, String email) throws NotFoundException
	{
		Person me = this.getMe();
		
		if (email == null)
		{
			// Subscribing with (or changing to) disabled delivery
			return this.admin.subscribe(listId, me.getId(), null, false);
		}
		else
		{
			EmailAddress addy = me.getEmailAddress(email);
			
			// If subscribing an address we do not currently own
			if (addy == null)
			{
				// TODO:  send a token that allows user to add and subscribe in one step
				return SubscribeResult.TOKEN_SENT;
			}
			else
			{
				return this.admin.subscribe(listId, me.getId(), email, false);
			}
		}
	}
	
	/**
	 * @see Receptionist#forgotPassword(String)
	 */
	public void forgotPassword(String email) throws NotFoundException
	{
		EmailAddress addy = this.dao.findEmailAddress(email);
		
		// TODO
		this.postOffice.sendPassword(null, null);
	}
}

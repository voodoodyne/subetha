/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.acct;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.acct.i.AccountMgrRemote;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.MySubscription;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.post.PostOffice;
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
		
		String[] addresses = new String[me.getEmailAddresses().size()];
		int i = 0;
		for (EmailAddress addy: me.getEmailAddresses().values())
		{
			addresses[i] = addy.getId();
			i++;
		}
		
		return new Self(
				me.getId(),
				me.getName(),
				addresses,
				me.isSiteAdmin(),
				Transmute.subscriptions(me.getSubscriptions().values())
			);
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
		//TODO
	}

	/**
	 * @see AccountMgr#addEmail(String)
	 */
	public void addEmail(String token) throws BadTokenException
	{
		//TODO
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
	public void subscribeAnonymousRequest(Long listId, String email, String name) throws NotFoundException, MessagingException
	{
		// Send a token to the person's account
		if (log.isDebugEnabled())
			log.debug("Requesting to subscribe " + email + " to list " + listId);
		
		MailingList mailingList = this.dao.findMailingList(listId);
		
		List<String> plainList = new ArrayList<String>();
		plainList.add(SUBSCRIBE_TOKEN_PREFIX);
		plainList.add(listId.toString());
		plainList.add(email);
		plainList.add(name);
		
		String cipherText = this.encryptor.encryptList(plainList);
		
		this.postOffice.sendConfirmSubscribeToken(mailingList, email, cipherText);
	}

	/**
	 * @see AccountMgr#subscribeAnonymous(String)
	 */
	@PermitAll
	public SubscribeResult subscribeAnonymous(String token) throws BadTokenException, NotFoundException
	{
		List<String> plainList = this.encryptor.decryptList(token);
		
		if (plainList.isEmpty() || !plainList.get(0).equals(SUBSCRIBE_TOKEN_PREFIX))
			throw new BadTokenException("Invalid token");
		
		Long listId = Long.valueOf(plainList.get(1));
		String email = plainList.get(2);
		String name = plainList.get(3);

		InternetAddress address = Transmute.internetAddress(email, name);
		
		return this.admin.subscribe(listId, address);
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
			return this.admin.subscribe(listId, me.getId(), null);
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
				return this.admin.subscribe(listId, me.getId(), email);
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

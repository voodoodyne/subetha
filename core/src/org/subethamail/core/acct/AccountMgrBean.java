/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.acct;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.acct.i.AccountMgrRemote;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;

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
	 */
	@EJB PostOffice postOffice;
	
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
				me.isSiteAdmin()
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
	 * @see AccountMgr#requestAddEmail(String)
	 */
	public void requestAddEmail(String newEmail) throws MessagingException
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
	 * @see AccountMgr#subscribe(Long, String)
	 */
	public void subscribe(Long listId, String email) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		Person me = this.getMe();
		EmailAddress addy = (email == null) ? null : me.getEmailAddress(email);
		
		// If subscribing an address we do not currently own
		if (email != null && addy == null)
		{
			// TODO:  send a token that allows user to add and subscribe in one step
			return;
		}
		
		if (me.isSubscribed(list))
		{
			// TODO: Make sure that the right email address is subscribed,
			// and if not, switch to this one.
			return;
		}
		
		// TODO:  maybe we need a subscription hold?
		
		Subscription sub = new Subscription(me, list, addy, list.getDefaultRole());
		this.dao.persist(sub);
		
		me.addSubscription(sub);
		list.getSubscriptions().add(sub);
	}
}

/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.acct;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.acct.i.AccountMgrRemote;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.PersonalEJB;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Person;

/**
 * Implementation of the AccountMgr interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="AccountMgr")
@SecurityDomain("subetha")
@RolesAllowed({"user"})
//@RunAs("god")
public class AccountMgrEJB extends PersonalEJB implements AccountMgr, AccountMgrRemote
{
	/** */
	private static Log log = LogFactory.getLog(AccountMgrEJB.class);

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
		for (EmailAddress addy: me.getEmailAddresses())
		{
			addresses[i] = addy.getId();
			i++;
		}
		
		return new Self(
				me.getId(),
				me.getName(),
				addresses
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
}

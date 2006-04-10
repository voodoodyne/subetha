/*
 * $Id: AccountMgrEJB.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/AccountMgrEJB.java $
 */

package org.subethamail.core.lists;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.core.lists.i.ListMgrRemote;
import org.subethamail.core.lists.i.MySubscription;
import org.subethamail.core.lists.i.SubscriberData;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the AccountMgr interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="ListMgr")
@SecurityDomain("subetha")
@RolesAllowed("user")
@RunAs("siteAdmin")
public class ListMgrBean extends PersonalBean implements ListMgr, ListMgrRemote
{
	/** */
	private static Log log = LogFactory.getLog(ListMgrBean.class);

	/**
	 */
	@EJB DAO dao;

	/**
	 * @see ListMgr#lookup(URL)
	 */
	@PermitAll
	public Long lookup(URL url) throws NotFoundException
	{
		return this.dao.findMailingList(url).getId();
	}
	
	/**
	 * @see ListMgr#getMySubscription(Long)
	 */
	@PermitAll
	public MySubscription getMySubscription(Long listId) throws NotFoundException
	{
		MailingList ml = this.dao.findMailingList(listId);
		Person me = this.getMe();
			
		return Transmute.mySubscription(me, ml);
	}

	/**
	 * @see ListMgr#subscribeAnonymous(Long, String, String)
	 */
	@PermitAll
	public SubscribeResult subscribeAnonymous(Long listId, String email, String name) throws NotFoundException
	{
		MailingList ml = this.dao.findMailingList(listId);
		
		// Send a token to the person's account
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see ListMgr#subscribeAnonymous(String)
	 */
	@PermitAll
	public SubscribeResult subscribeAnonymous(String token) throws BadTokenException, NotFoundException
	{
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see ListMgr#subscribeMe(Long, String)
	 */
	public SubscribeResult subscribeMe(Long listId, String email) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		Person me = this.getMe();
		EmailAddress addy = (email == null) ? null : me.getEmailAddress(email);
		
		// If subscribing an address we do not currently own
		if (email != null && addy == null)
		{
			// TODO:  send a token that allows user to add and subscribe in one step
			return SubscribeResult.TOKEN_SENT;
		}
		
		Subscription sub = me.getSubscription(listId);
		if (sub != null)
		{
			// If we're already subscribed, maybe we want to change the
			// delivery address.
			sub.setDeliverTo(addy);
			
			return SubscribeResult.OK;
		}
		else
		{
			// TODO:  maybe we need a subscription hold?
			
			sub = new Subscription(me, list, addy, list.getDefaultRole());
			this.dao.persist(sub);
			
			me.addSubscription(sub);
			list.getSubscriptions().add(sub);
			
			return SubscribeResult.OK;
		}
	}
	
	public List<SubscriberData> getSubscribers(Long listId) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		Set<Subscription> listSubscriptions = list.getSubscriptions();
		return Transmute.getSubscriberDataList(listSubscriptions);
	}
}

/*
 * $Id: PersonalBean.java 735 2006-08-20 04:21:14Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/PersonalBean.java $
 */

package org.subethamail.core.util;

import java.security.Principal;

import javax.inject.Current;
import javax.inject.manager.Manager;

import org.subethamail.common.NotFoundException;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;

import com.caucho.security.SecurityContext;
import com.caucho.security.SecurityContextException;

/**
 * Base class for session EJBs which are called by authenticated
 * users.  Provides convenient methods to access who the user is.
 * 
 * Note that the security prinicpal is the stringified person id.
 * 
 * These methods should be fast because they only do primary key lookups
 * out of the 2nd level cache.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class PersonalBean extends EntityManipulatorBean
{
	/** */
	@Current protected Manager wbManager;
	//@Current protected SessionContext sessionContext;

	
//	@Current
//	public void setSessionContext(SessionContext sc){
//		if(this.sessionContext == null) this.sessionContext = sc;
//	}
//	

	/**
	 * Obtains my personId from the security context, or null
	 * if there is no security context
	 */
	protected Long getMyId()
	{
		//TODO: Bug this with resin. The SessionContext was always null!
		Principal p = null;
	    try {
	        p = SecurityContext.getUserPrincipal();
	      } catch (SecurityContextException e) {
	    	  throw new RuntimeException(e);
	      }
		//Principal p = this.sessionContext.getCallerPrincipal();
		//Principal p = wbManager.getInstanceByType(Principal.class);
		
		String name = p.getName();
		
		if (name == null)
		{
			return null;
		}
		else
		{
			return Long.valueOf(name);
		}
	}
	
	/**
	 * Get the person associated with the current security context, or null if there
	 * is no security context.
	 */
	protected Person getMe()
	{
		Long myId = this.getMyId();
		
		if (myId == null)
			return null;
		else
			return this.em.find(Person.class, myId);
	}

	/**
	 * Helper method throws an exception if you don't have the permission on the list.
	 */
	protected MailingList getListFor(Long listId, Permission check) throws NotFoundException, PermissionException
	{
		return this.getListFor(listId, check, this.getMe());
	}
	
	/**
	 * Useful if you already have retreived the Me object.
	 * 
	 * @see PersonalBean#getListFor(Long, Permission)
	 */
	protected MailingList getListFor(Long listId, Permission check, Person me) throws NotFoundException, PermissionException
	{
		MailingList list = this.em.get(MailingList.class, listId);
	
		list.checkPermission(me, check);
		
		return list;
	}
	
	/**
	 * Helper method throws an exception if you don't have the permission on the list.
	 */
	protected Mail getMailFor(Long mailId, Permission check) throws NotFoundException, PermissionException
	{
		return this.getMailFor(mailId, check, this.getMe());
	}
	
	/**
	 * Useful if you already have retreived the Me object.
	 * 
	 * @see PersonalBean#getMailFor(Long, Permission)
	 */
	protected Mail getMailFor(Long mailId, Permission check, Person me) throws NotFoundException, PermissionException
	{
		Mail mail = this.em.get(Mail.class, mailId);
	
		mail.getList().checkPermission(me, check);
		
		return mail;
	}

	/**
	 * Requires that you have Permission.EDIT_ROLES
	 */
	protected Role getRoleForEdit(Long roleId) throws NotFoundException, PermissionException
	{
		Role role = this.em.get(Role.class, roleId);
		
		role.getList().checkPermission(this.getMe(), Permission.EDIT_ROLES);
		
		return role;
	}
	
	/**
	 * Helper method throws an exception if you don't have the permission on the list
	 * associated with the subscription.  Note that the personId is to identify the
	 * subscription, NOT to check permissions.
	 * 
	 * @throws NotFoundException if person isn't subscribed to that list.
	 */
	protected Subscription getSubscriptionFor(Long listId, Long personId, Permission check, Person me) throws NotFoundException, PermissionException
	{
		Person pers = this.em.get(Person.class, personId);
		
		Subscription sub = pers.getSubscription(listId);
		if (sub == null)
			throw new NotFoundException("Person is not subscribed to that list id");
		
		sub.getList().checkPermission(me, check);

		return sub;
	}
}
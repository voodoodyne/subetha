/*
 * $Id: PersonalEJB.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/PersonalEJB.java $
 */

package org.subethamail.core.util;

import java.security.Principal;

import javax.annotation.EJB;
import javax.annotation.Resource;
import javax.ejb.SessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Permission;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.dao.DAO;

/**
 * Base class for session EJBs which are called by authenticated
 * users.  Provides convenient methods to access who the user is.
 * 
 * Note that the security prinicpal is an email address.  Therefore
 * it's possible for multiple principals to map to the same Person
 * object.  This shouldn't cause any unusual behavior.
 * 
 * These methods should be fast because they only do primary key lookups
 * out of the 2nd level cache.
 * 
 * @author Jeff Schnitzer
 */
public class PersonalBean
{
	/** */
	private static Log log = LogFactory.getLog(PersonalBean.class);

	/** */
	@Resource protected SessionContext sessionContext;
	
	/** */
	@EJB protected DAO dao;
	
	/**
	 * Obtains my address from the security context, or null
	 * if there is no security context
	 */
	protected EmailAddress getMyAddress()
	{
		try
		{
			Principal p = this.sessionContext.getCallerPrincipal();
			
			String name = p.getName();
			
			if (name == null)
			{
				return null;
			}
			else		
			{
				try
				{
					return this.dao.findEmailAddress(name);
				}
				catch (NotFoundException ex)
				{
					return null;
				}
			}
		}
		catch (IllegalStateException ex)
		{
			// TODO:  this behavior is not exactly ejb3 spec compliant, so it
			// might change in the future.  beware.
			return null;
		}
	}
	
	/**
	 * Get the person associated with the current security context, or null if there
	 * is no security context.
	 */
	protected Person getMe()
	{
		EmailAddress addy = this.getMyAddress();
		
		if (addy == null)
			return null;
		else
			return addy.getPerson();
	}

	/**
	 * Helper method throws an exception if you don't have the permission on the list.
	 */
	protected MailingList getListFor(Long listId, Permission check) throws NotFoundException
	{
		MailingList list = this.dao.findMailingList(listId);
		Role role = list.getRoleFor(this.getMe());
	
		if (! role.getPermissions().contains(check))
			throw new IllegalStateException("Not allowed");
		
		return list;
	}
}

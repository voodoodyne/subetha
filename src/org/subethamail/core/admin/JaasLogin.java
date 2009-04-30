/*
 * $Id: JaasLogin.java 735 2006-08-20 04:21:14Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/JaasLogin.java $
 */

package org.subethamail.core.admin;

import javax.ejb.Local;

import org.subethamail.common.NotFoundException;
import org.subethamail.entity.Person;


/**
 * Trivial bean, intended to be called by JAAS LoginModules, that
 * allows fetching some data about the user.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface JaasLogin
{
	/** */
	public static final String JNDI_NAME = "subetha/JaasLogin/local";

	/**
	 * Get the naked Person object 
	 */
	public Person getPerson(Long id) throws NotFoundException;
}

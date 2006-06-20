/*
 * $Id$
 * $URL$
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
	 * Get the naked Person object associated with the email.] 
	 */
	public Person getPersonForEmail(String email) throws NotFoundException;
}

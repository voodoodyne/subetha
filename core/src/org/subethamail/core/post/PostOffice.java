/*
 * $Id: PostOffice.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/post/PostOffice.java $
 */

package org.subethamail.core.post;

import javax.ejb.Local;
import javax.mail.MessagingException;

import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;

/**
 * Sends outbound email with a variety of templates.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface PostOffice
{
	/** */
	public static final String JNDI_NAME = "subetha/PostOffice/local";
		
	/**
	 * Sends a special token that will subscribe a user to a list.
	 * 
	 * @param list is the context of the request, defined by which
	 *  website we are visiting.
	 */
	public void sendSubscribeToken(MailingList list, String email, String token) throws MessagingException;

	/**
	 * In the case of a forgotten password, this sends a piece of email to the
	 * specified member with the password.
	 * 
	 * @param list is the context of the request, defined by which
	 *  website we are visiting.
	 */
	public void sendPassword(MailingList list, EmailAddress addy) throws MessagingException;
	
}

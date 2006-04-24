/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.post;

import javax.ejb.Local;

import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;

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
	 * Notifies the user that they are now the pround owner of a
	 * bouncing new baby mailing list.
	 */
	public void sendOwnerNewMailingList(EmailAddress address, MailingList list);
		
	/**
	 * Sends a special token that will subscribe a user to a list.
	 * 
	 * @param list which mailing list we are subscribing to.
	 */
	public void sendConfirmSubscribeToken(MailingList list, String email, String token);

	/**
	 * Informs the user that they are now subscribed to a mailing list.
	 * 
	 * @param deliverTo might be null in the case of disabled delivery, in
	 *  which case a random email address of the person gets the notice.
	 */
	public void sendSubscribed(MailingList list, Person who, EmailAddress deliverTo);

	/**
	 * In the case of a forgotten password, this sends a piece of email to the
	 * specified member with the password.
	 * 
	 * @param list is the context of the request, defined by which
	 *  website we are visiting.
	 */
	public void sendPassword(MailingList list, EmailAddress addy);

	/**
	 * Sends a token to the address which will merge that address (and
	 * any account that may exist at that address) into the person.
	 */
	public void sendAddEmailToken(Person me, String email, String token);
	
}

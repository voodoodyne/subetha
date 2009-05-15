/*
 * $Id: PostOffice.java 653 2006-06-19 07:50:48Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/post/PostOffice.java $
 */

package org.subethamail.core.post;

import javax.ejb.Local;

import org.subethamail.common.SubEthaMessage;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;

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
	public void sendOwnerNewMailingList(MailingList relevantList, EmailAddress address);
		
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
	public void sendSubscribed(MailingList relevantList, Person who, EmailAddress deliverTo);

	/**
	 * In the case of a forgotten password, this sends a piece of email to the
	 * specified member with the password.
	 */
	public void sendPassword(EmailAddress addy);

	/**
	 * Sends a token to the address which will merge that address (and
	 * any account that may exist at that address) into the person.
	 */
	public void sendAddEmailToken(Person me, String email, String token);

	/**
	 * Notifies the moderator that someone wants to subscribe and needs approval (or not).
	 */
	public void sendModeratorSubscriptionHeldNotice(EmailAddress moderator, SubscriptionHold hold);

	/**
	 * Sends mail to the address letting the person know that their message is
	 * waiting for approval.
	 */
	public void sendPosterMailHoldNotice(MailingList relevantList, String posterEmail, Mail mail, String holdMsg);
	
	/**
	 * Sends mail to the address letting the moderator know that a message is
	 * waiting for approval.
	 */
	public void sendModeratorMailHoldNotice(EmailAddress moderator, MailingList relevantList, Mail mail, SubEthaMessage msg, String holdMsg);

	/**
	 * Notify the moderator that someone new subscribed or unsubscribed to the list.
	 * 
	 * @param unsub is true if this is an unsubscription result.
	 */
	public void sendModeratorSubscriptionNotice(EmailAddress moderator, Subscription sub, boolean unsub);

	/**
	 * Turns on test mode.
	 * 
	 * @return if in test mode.
	 */
	public boolean enableTestMode();

	/**
	 * Turns off test mode.
	 * 
	 * @return if in test mode.
	 */
	public boolean disableTestMode();
	
	
}

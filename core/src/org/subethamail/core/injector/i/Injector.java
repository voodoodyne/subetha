/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.injector.i;

import javax.ejb.Local;
import javax.mail.MessagingException;

/**
 * Interface for injecting raw mail into the system.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Injector
{
	/** */
	public static final String JNDI_NAME = "subetha/Injector/local";

	/**
	 * Processes of a piece of raw mail in rfc822 format.
	 * 
	 * Mail can be anything - a message to a mailing list, a bounce
	 * message, or something else.  It will be processed accordingly.
	 * 
	 * @param toAddress is an rfc822-compliant destination for the mail.
	 * @param mailData is the rfc822-compliant message.
	 * 
	 * @throws MessagingException if the message data or toAddress could not be parsed.
	 * @throws AddressUnknownException if the address is not something we know what to
	 *  do with; ie it is not a known list address or a VERP bounce.
	 */
	public void inject(String toAddress, byte[] mailData) throws MessagingException, AddressUnknownException;
}


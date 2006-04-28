/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector.i;

import java.io.IOException;
import java.io.InputStream;

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
	 * @return true of the address is intended for us, ie, it is for
	 *  a known mailing list or it is a VERP bounce.
	 */
	public boolean accept(String toAddress) throws MessagingException;

	/**
	 * Processes of a piece of raw mail in rfc822 format.
	 * 
	 * Mail can be anything - a message to a mailing list, a bounce
	 * message, or something else.  It will be processed accordingly.
	 * 
	 * @param fromAddress is the rfc822-compliant envelope sender.
	 * @param toAddress is the rfc822-compliant envelope recipient.
	 * @param mailData is the rfc822-compliant message.
	 *
	 * @return true if the message was handled, false if message is not for us
	 * 
	 * @throws MessagingException if the message data or toAddress could not be parsed.
	 */
	public boolean inject(String fromAddress, String toAddress, InputStream mailData) throws MessagingException, IOException;

	/**
	 * Convenience method for remote clients.  Most inputStream implementations
	 * are not serializable.
	 */
	public boolean inject(String fromAddress, String toAddress, byte[] mailData) throws MessagingException, IOException;
}


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
	public static final String JNDI_NAME = "Injector/local";

	/**
	 * Processes of a piece of raw mail.  The mail is expected
	 * to be in the format that a MDA would expect, headers first
	 * then an empty line then body.
	 * 
	 * Mail can be anything - a message to a mailing list, a bounce
	 * message, or some random junk.  It will be processed accordingly.
	 * If the mail is not significant to us, it will be silently dropped.
	 * 
	 * @param toAddress is an rfc222-compliant destination for the mail.  If the
	 *  address is not a valid mailing list, the mail is logged and dropped.
	 * @param mailData is the raw (possibly MIME) mail content, including headers.
	 * @throws MessagingException if the message data or toAddress could not be parsed.
	 * 
	 * TODO:  consider an exception instead of silently dropping irrelevant mail?
	 */
	public void inject(String toAddress, byte[] mailData) throws MessagingException;
	
	/**
	 * A test method that logs the deconstruction of a message.
	 */
	public void log(byte[] mailData) throws MessagingException;
}


/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.injector.i;

import java.io.InputStream;
import java.util.Date;

import javax.context.ApplicationScoped;
import javax.ejb.Local;

import org.subethamail.common.NotFoundException;
import org.subethamail.common.io.LimitExceededException;

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
	 *  
	 * @throws a RuntimeException if the address was invalid
	 */
	public boolean accept(String toAddress);

	/**
	 * Processes of a piece of raw mail in rfc822 format.
	 * 
	 * Mail can be anything - a message to a mailing list, a bounce
	 * message, or something else.  It will be processed accordingly.
	 *
	 * If the message is a duplicate, a new messageId will be assigned 
	 * and the message will be saved. If you want to change this behaviour,
	 * use the version that takes an ignoredDuplicates flag.
	 * 
	 * @param fromAddress is the rfc822-compliant envelope sender.
	 * @param toAddress is the rfc822-compliant envelope recipient.
	 * @param mailData is the rfc822-compliant message.
	 *
	 * @return true if the message was handled, false if message is not for us
	 * 
	 * @throws LimitExceededException if the input data was too large
	 * @throws a RuntimeException if there is a problem with the input data
	 */
	public boolean inject(String fromAddress, String toAddress, InputStream mailData) throws LimitExceededException;

	/**
	 * Convenience method for remote clients.  Most inputStream implementations
	 * are not serializable.
	 */
	public boolean inject(String fromAddress, String toAddress, byte[] mailData) throws LimitExceededException;
	
	/**
	 * Imports of a piece of raw mail in rfc822 format into the archives
	 * of a particular list.
	 * 
	 * @param ignoreDuplicate if true will skip messages whose message-id already exists
	 * @param fallbackDate is the date to use only if a date cannot be extracted from the message headers
	 * 
	 * @return the sent date of the message, if one could be identified 
	 *
	 * @throws NotFoundException if the list id is not a valid list
	 */
	public Date importMessage(Long listId, String envelopeSender, InputStream mailData, boolean ignoreDuplicate, Date fallbackDate) throws NotFoundException;
}


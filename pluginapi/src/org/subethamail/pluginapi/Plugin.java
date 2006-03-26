/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Interface that plugins must implement. 
 * 
 * @author Jeff Schnitzer
 */
public interface Plugin
{
	/**
	 * Allows plugin to manipulate the message upon injection, immediately
	 * after it has been decoded by JavaMail but before any further processing
	 * has been done.
	 * 
	 * @throws IgnoreException if the message should be silently dropped.
	 * @throws MessagingException if there was an error processing the message,
	 *  or if for any reason message receipt should be aborted.
	 */
	public void onInject(MimeMessage msg) throws IgnoreException, MessagingException;
	
	/**
	 * Allows plugin to manipulate the message as it is being sent outbound.  This
	 * is called prior to sending, but before attachments are reconstituted in the
	 * outbound message.  Any attachments in the message will references be of type
	 * x-subetha/attachment-ref. 
	 *  
	 * @throws IgnoreException if the message should not be sent.
	 */
	public void onSendBeforeAttaching(MimeMessage msg) throws IgnoreException;
	
	/**
	 * Allows plugin to manipulate the message as it is being sent outbound.  This
	 * is the last hook prior to a message being sent, after attachment references
	 * have been replaced with the genuine contents.
	 *  
	 * @throws IgnoreException if the message should not be sent.
	 */
	public void onSendAfterAttaching(MimeMessage msg) throws IgnoreException;
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i;

import java.util.Map;

import javax.mail.MessagingException;

import org.subethamail.common.SubEthaMessage;

/**
 * Interface that mail filters must implement.  Mail filters get
 * several opportunities to modify a piece of inbound or outbound mail. 
 * 
 * @author Jeff Schnitzer
 */
public interface Filter
{
	/**
	 * @return a nice short name for the filter.
	 */
	public String getName();
	
	/**
	 * @return a reasonably lengthy description of the filter.
	 */
	public String getDescription();
	
	/**
	 * Gets the list of parameters that this filter supports.
	 */
	public FilterParameter[] getParameters();
	
	/**
	 * Allows filter to manipulate the message upon injection, immediately
	 * after it has been decoded by JavaMail but before any further processing
	 * has been done.
	 * 
	 * @throws IgnoreException if the message should be silently dropped.  This will
	 *  halt execution of the plugin stack.
	 * @throws HoldException if the message should be held for
	 *  administrative (not self) moderation.  The message string is significant.
	 *  Note that this will not halt execution of the filter stack.
	 * @throws MessagingException if there was an error processing the message,
	 *  or if for any reason message receipt should be aborted.  Halts execution
	 *  of the filter stack.
	 */
	public void onInject(SubEthaMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException;
	
	/**
	 * Allows filter to manipulate the message as it is being sent outbound.  This
	 * is called prior to sending, but before attachments are reconstituted in the
	 * outbound message.  Any attachments in the message will references be of type
	 * x-subetha/attachment-ref. 
	 *  
	 * @throws IgnoreException if the message should not be sent.  Halts execution
	 *  of the filter stack.
	 */
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws IgnoreException, MessagingException;

	/**
	 * This method is for Filter authors to override to allow them to document
	 * the items that they place into a context for the FilterContext.expand()
	 * method. They should override this method by <strong>calling the
	 * super.getDocumentation() method first</strong> (in order to get the
	 * default documentation Map) and returning the resulting Map object that
	 * represents a key/value pair of String objects with the key being the
	 * class/property and the value being the documentation of what is in the
	 * key. For example: key: ${list.name} value: The list name.
	 * 
	 * @return A ball full of wax.
	 */
	public Map<String, String> getDocumentation();

}

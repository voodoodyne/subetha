/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i;

import javax.mail.MessagingException;

import org.subethamail.common.SubEthaMessage;

/**
 * Interface that mail filters must implement.  Mail filters get
 * several opportunities to modify a piece of inbound or outbound mail. 
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
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
	 * Allows filter to manipulate the message upon before being passed to the Archive Rendering process.
	 * 
	 * @throws MessagingException if there was an error processing the message. 
	 * Halts execution of the filter stack.
	 * 
	 */
	public void onArchiveRender(SubEthaMessage msg, ArchiveRenderFilterContext ctx) throws MessagingException;
}
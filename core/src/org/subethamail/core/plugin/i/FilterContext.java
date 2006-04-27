/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.subethamail.core.lists.i.ListData;


/**
 * Context for filter execution, providing information from the container
 * such as what list is being process and what the filter arguments are.
 * 
 * @author Jeff Schnitzer
 */
public interface FilterContext
{
	/**
	 * Get the data about a mailing list.
	 * @return the data about a mailing list
	 */
	public ListData getListData();	
	
	/**
	 * Get the current message being processed
	 */
	public MimeMessage getMimeMessage();	
	
	/**
	 * This method will use Velocity to process data using the passed in objects
	 * for the context. By default, two objects (MailSummary and ListData) 
	 * are made available as $mail and $list. If you try to pass in a context
	 * with those names, they will be ignored.
	 *
	 * @return the expanded string.
	 */
	public String expand(String data, Map<String, Object> context);

	/**
	 * @return the correctly-typed value of the named parameter. 
	 */
	public Object getArgument(String name);
}
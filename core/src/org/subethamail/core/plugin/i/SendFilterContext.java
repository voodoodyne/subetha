/*
 * $Id: FilterContext.java 222 2006-04-27 21:47:32Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/plugin/i/FilterContext.java $
 */

package org.subethamail.core.plugin.i;

import org.subethamail.core.lists.i.MailSummary;


/**
 * Adds some extra information available at sending time.
 * 
 * @author Jeff Schnitzer
 */
public interface SendFilterContext extends FilterContext
{
	/**
	 * @return the id of the mail that is about to be sent.
	 */
	public Long getMailId();
	
	/**
	 * @return the entire thread hierarchy (as it exists in the server so far)
	 *  in the form of MailSummary objects.  The current mail will be in this
	 *  tree.
	 */
	public MailSummary getThreadRoot();
}
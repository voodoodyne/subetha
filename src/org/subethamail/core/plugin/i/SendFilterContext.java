/*
 * $Id: SendFilterContext.java 902 2007-01-15 03:00:15Z skot $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/plugin/i/SendFilterContext.java $
 */

package org.subethamail.core.plugin.i;

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
}
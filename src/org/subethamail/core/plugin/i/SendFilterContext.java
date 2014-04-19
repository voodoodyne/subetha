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
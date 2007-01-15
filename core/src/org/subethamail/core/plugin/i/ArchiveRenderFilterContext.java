/*
 * $Id: SendFilterContext.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/plugin/i/SendFilterContext.java $
 */

package org.subethamail.core.plugin.i;



/**
 * Adds some extra information available at archive rendering time.
 * 
 * @author Scott Hernandez
 */
public interface ArchiveRenderFilterContext extends SendFilterContext
{
	/**
	 * @return the id of the mail that is about to be rendered.
	 */
	public Long getMailId();
}
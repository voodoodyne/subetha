/*
 * $Id: GenericFilter.java 902 2007-01-15 03:00:15Z skot $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/plugin/i/helper/GenericFilter.java $
 */

package org.subethamail.core.plugin.i.helper;

import javax.mail.MessagingException;

import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.*;

/**
 * Trivial filter implementation that has no parameters and does
 * nothing.  Override the methods that you want to change.   
 * 
 * @author Jeff Schnitzer
 */
public abstract class GenericFilter implements Filter
{
	/**
	 * @see Filter#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		final FilterParameter[] defs = new FilterParameter[0];
		return defs;
	}
	
	/**
	 * @see Filter#onInject(SubEthaMessage, FilterContext)
	 */
	public void onInject(SubEthaMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
	}
	
	/**
	 * @see Filter#onSend(SubEthaMessage, SendFilterContext)
	 */
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws IgnoreException, MessagingException
	{
	}

	/**
	 * @see Filter#onArchiveRender(SubEthaMessage, ArchiveRenderFilterContext)
	 */
	public void onArchiveRender(SubEthaMessage msg, ArchiveRenderFilterContext ctx) throws MessagingException
	{
		try 
		{
			onSend(msg, ctx);
		}
		catch (IgnoreException ex) 
		{ 
			throw new MessagingException("Message should be ignored...",ex);
		}
	}
}

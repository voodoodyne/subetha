package org.subethamail.core.plugin.i.helper;

import javax.mail.MessagingException;

import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.ArchiveRenderFilterContext;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.SendFilterContext;

/**
 * Trivial filter implementation that has no parameters and does
 * nothing.  Override the methods that you want to change.   
 * 
 * @author Jeff Schnitzer
 */
public abstract class GenericFilter implements Filter
{
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		final FilterParameter[] defs = new FilterParameter[0];
		return defs;
	}
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#onInject(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.FilterContext)
	 */
	public void onInject(SubEthaMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
	}
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#onSend(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.SendFilterContext)
	 */
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws IgnoreException, MessagingException
	{
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#onArchiveRender(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.ArchiveRenderFilterContext)
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

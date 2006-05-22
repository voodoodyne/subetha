/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import javax.mail.MessagingException;

import org.subethamail.common.SubEthaMessage;
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
public abstract class GenericFilter extends AbstractFilter
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
}

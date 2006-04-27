/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import javax.mail.MessagingException;

import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;

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
	 * @see Filter#onInject(FilterContext)
	 */
	public void onInject(FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
	}
	
	/**
	 * @see Filter#onSendBeforeAttaching(FilterContext)
	 */
	public void onSendBeforeAttaching(FilterContext ctx) throws IgnoreException
	{
	}
	
	/**
	 * @see Filter#onSendAfterAttaching(FilterContext)
	 */
	public void onSendAfterAttaching(FilterContext ctx) throws IgnoreException
	{
	}
}

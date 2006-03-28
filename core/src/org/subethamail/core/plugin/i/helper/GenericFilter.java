/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i.helper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
public class GenericFilter extends AbstractFilter
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
	 * @see Filter#onInject(MimeMessage, FilterContext)
	 */
	public void onInject(MimeMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
	}
	
	/**
	 * @see Filter#onSendBeforeAttaching(MimeMessage, FilterContext)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, FilterContext ctx) throws IgnoreException
	{
	}
	
	/**
	 * @see Filter#onSendAfterAttaching(MimeMessage, FilterContext)
	 */
	public void onSendAfterAttaching(MimeMessage msg, FilterContext ctx) throws IgnoreException
	{
	}
}

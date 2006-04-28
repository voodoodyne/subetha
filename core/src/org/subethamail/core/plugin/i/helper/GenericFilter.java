/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private Map<String, String> map;
	
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
	 * @see Filter#getDocumentation()
	 */
	public Map<String, String> getDocumentation()
	{
		if (map == null)
		{
			map = new ConcurrentHashMap<String, String>();
			map.put("${list.name}", "The name of this mailing list.");
			map.put("${list.description}", "The description of this mailing list.");
			map.put("${list.email}", "The email address of this mailing list.");
			map.put("${list.url}", "The url of this mailing list.");
			map.put("${list.id}", "The numeric id of this mailing list.");
			map.put("${mail.subject}", "The mail subject.");		
		}
		return map;
	}
}

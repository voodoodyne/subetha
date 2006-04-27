/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.filter;

import java.io.StringWriter;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.FilterArgument;
import org.subethamail.entity.MailingList;


/**
 * Implementation of the FilterContext
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
class FilterContextImpl implements FilterContext
{
	/** */
	EnabledFilter enabledFilter;
	Filter filter;
	MimeMessage msg;
	ListData listData;
	
	/** 
	 */
	public FilterContextImpl(EnabledFilter enabledFilter, Filter filter, MimeMessage msg)
	{
		this.enabledFilter = enabledFilter;
		this.filter = filter;
		this.msg = msg;
	}
	
	/**
	 * @see FilterContext#getListData()
	 */
	public ListData getListData()
	{
		// internally cache the object for speed
		if (this.listData == null)
		{
			MailingList ml = this.enabledFilter.getList();
			this.listData = Transmute.mailingList(ml);
		}
		return this.listData;
	}
	
	/**
	 * @see FilterContext#getMimeMessage()
	 */
	public MimeMessage getMimeMessage()
	{
		return this.msg;
	}

	/**
	 * @see FilterContext#getArgument(String)
	 */
	public Object getArgument(String name)
	{
		FilterArgument arg = this.enabledFilter.getArguments().get(name);
		if (arg == null)
			return null;
		else
			return arg.getValue();
	}

	/**
	 * @see FilterContext#expand(String, Map<String, Object> context)
	 */
	public String expand(String template, Map<String, Object> context)
	{
		VelocityContext vctx = new VelocityContext();
	    for (Map.Entry<String, Object> e : context.entrySet())
	    {
	    	if (e.getKey().equals("mail") || e.getKey().equals("list"))
	    		continue;
	    	vctx.put(e.getKey(),e.getValue());
	    }
	    vctx.put("mail", this.msg);
	    vctx.put("list", this.getListData());

	    StringWriter writer = new StringWriter(4096);
		try
		{
			Velocity.mergeTemplate(template, "UTF-8", vctx, writer);
		}
		catch (Exception e1)
		{
			// TODO: how to handle this?
		}

		return writer.toString();
	}
}
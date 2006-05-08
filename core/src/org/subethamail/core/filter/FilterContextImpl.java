/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.filter;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.subethamail.common.SubEthaMessage;
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
	SubEthaMessage msg;
	ListData listData;
	
	/** 
	 */
	public FilterContextImpl(EnabledFilter enabledFilter, Filter filter, SubEthaMessage msg)
	{
		this.enabledFilter = enabledFilter;
		this.filter = filter;
		this.msg = msg;
	}
	
	/**
	 * @see FilterContext#getList()
	 */
	public ListData getList()
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
	 * @see FilterContext#expand(String)
	 */
	public String expand(String template)
	{
		return this.expand(template, null);
	}

	/**
	 * @see FilterContext#expand(String, Map<String, Object> context)
	 */
	public String expand(String template, Map<String, Object> context)
	{
		VelocityContext vctx = new VelocityContext();
		
		if (context != null)
		{
		    for (Map.Entry<String, Object> e: context.entrySet())
		    {
		    	if (e.getKey().equals("mail") || e.getKey().equals("list"))
		    		continue;
		    	vctx.put(e.getKey(),e.getValue());
		    }
		}
		
	    vctx.put("mail", this.msg);
	    vctx.put("list", this.getList());

	    Writer writer = new BufferedWriter(new StringWriter(1024));
		try
		{
			Velocity.evaluate(vctx, writer, "subetha", template);
		}
		catch (Exception e1)
		{
			// TODO: how to handle this?
		}

		return writer.toString();
	}
}
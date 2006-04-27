/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.filter;

import java.util.Map;

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
 */
class FilterContextImpl implements FilterContext
{
	/** */
	EnabledFilter enabledFilter;
	Filter filter;
	
	/** 
	 */
	public FilterContextImpl(EnabledFilter enabledFilter, Filter filter)
	{
		this.enabledFilter = enabledFilter;
		this.filter = filter;
	}
	
	/**
	 * @see FilterContext#getListData(String)
	 */
	public ListData getListData()
	{
		MailingList ml = this.enabledFilter.getList();
		return Transmute.mailingList(ml);
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
	public String expand(String data, Map<String, Object> context)
	{
		return "";
	}
}
/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.filter;

import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.FilterArgument;


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
	 * @see FilterContext#getListAddress()
	 */
	public String getListAddress()
	{
		return this.enabledFilter.getList().getEmail();
	}

	/**
	 * @see FilterContext#getListURL()
	 */
	public String getListURL()
	{
		return this.enabledFilter.getList().getUrl();
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

}
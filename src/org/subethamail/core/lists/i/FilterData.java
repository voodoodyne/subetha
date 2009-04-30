/*
 * $Id: FilterData.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/FilterData.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;

import org.subethamail.core.plugin.i.FilterParameter;

/**
 * Information about an available filter.  Note that these
 * are not entities, but modules loaded into the application.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class FilterData implements Serializable
{
	/** Class name identifies a filter type */
	String className;
	String name;
	String description;
	FilterParameter[] parameters;

	protected FilterData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/**
	 */
	public FilterData(String className, String name, String description, FilterParameter[] parameters)
	{
		this.className = className;
		this.name = name;
		this.description = description;
		this.parameters = parameters;
	}

	/** */
	public String getClassName()
	{
		return this.className;
	}

	/** */
	public String getDescription()
	{
		return this.description;
	}

	/** */
	public String getName()
	{
		return this.name;
	}

	/** */
	public FilterParameter[] getParameters()
	{
		return this.parameters;
	}
}

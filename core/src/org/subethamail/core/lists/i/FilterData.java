/*
 * $Id$
 * $URL$
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

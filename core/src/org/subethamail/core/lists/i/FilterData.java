/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
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

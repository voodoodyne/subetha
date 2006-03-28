/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i.helper;

import org.subethamail.core.plugin.i.FilterParameter;


/**
 * Simple implementation of FilterParameter
 * 
 * @author Jeff Schnitzer
 */
public class FilterParameterImpl implements FilterParameter
{
	String name;
	String description;
	Class type;
	Object defaultValue;
	
	/** */
	public FilterParameterImpl(String name, String description, Class type, Object defaultValue)
	{
		this.name = name;
		this.description = description;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 */
	public String getName() { return this.name; }
	
	/**
	 */
	public String getDescription() { return this.description; }
	
	/**
	 */
	public Class getType() { return this.type; }
	
	/**
	 */
	public Object getDefaultValue() { return this.defaultValue; }
}

/*
 * $Id$
 * $URL$
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
	int textLines;
	boolean expanded;
	
	/** */
	public FilterParameterImpl(String name, String description, Class type, Object defaultValue)
	{
		this.name = name;
		this.description = description;
		this.type = type;
		this.defaultValue = defaultValue;
		
		if (this.defaultValue != null && !this.defaultValue.getClass().equals(type))
			throw new IllegalArgumentException("Type mismatch for parameter " + name
					+ "; type is " + type.getName() + " but default value has type "
					+ defaultValue.getClass().getName());
	}

	/**
	 * Assumes java.lang.String 
	 */
	public FilterParameterImpl(String name, String description, String defaultValue, int textLines, boolean expanded)
	{
		this(name, description, String.class, defaultValue);
		
		this.textLines = textLines;
		this.expanded = expanded;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#getName()
	 */
	public String getName() { return this.name; }
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#getDescription()
	 */
	public String getDescription() { return this.description; }
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#getType()
	 */
	public Class getType() { return this.type; }
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#getDefaultValue()
	 */
	public Object getDefaultValue() { return this.defaultValue; }

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#getTextLines()
	 */
	public int getTextLines()
	{
		return this.textLines;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterParameter#isExpanded()
	 */
	public boolean isExpanded()
	{
		return this.expanded;
	}
}

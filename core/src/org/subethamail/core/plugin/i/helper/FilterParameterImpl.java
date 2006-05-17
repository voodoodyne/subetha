/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import java.util.HashMap;
import java.util.Map;
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
	Map<String, String> documentation;

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
	 * Assumes java.lang.String for the Class type
	 * Assumes Documentation is null.
	 * If expanded is true then the default docs will be returned.
	 * If expanded is false, then getDocumentation() will return null.
	 */
	public FilterParameterImpl(String name, String description, String defaultValue, int textLines, boolean expanded)
	{
		this(name, description, defaultValue, textLines, expanded, null);
	}

	/**
	 * Assumes java.lang.String for the Class type
	 * Documentation can be null.
	 * If expanded is true, and documentation is null, then the default docs will be returned.
	 * If expanded is true and documention is not null, then the default docs will be appended
	 * to the passed in documentation.
	 * If expanded is false, then getDocumentation() will return null.
	 */
	public FilterParameterImpl(String name, String description, String defaultValue, int textLines, boolean expanded, Map<String, String> documentation)
	{
		this(name, description, String.class, defaultValue);
		
		this.textLines = textLines;
		this.expanded = expanded;
		if (this.expanded == true)
			initDocumentation();
		if (documentation != null)
			this.documentation.putAll(documentation);
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

	/**
	 * 
	 */
	public Map<String, String> getDocumentation()
	{
		// we only want to init the documentation if it this
		// parameter is marked as to be expanded.
		if (expanded && documentation == null)
		{
			initDocumentation();
		}
		return documentation;
	}

	/**
	 * Initializes the default set of documentation.
	 */
	protected void initDocumentation()
	{
		if (documentation == null)
		{
			documentation = new HashMap<String, String>();
			documentation.put("${list.name}", "The name of this mailing list.");
			documentation.put("${list.description}", "The description of this mailing list.");
			documentation.put("${list.email}", "The email address of this mailing list.");
			documentation.put("${list.url}", "The url of this mailing list.");
			documentation.put("${list.id}", "The numeric id of this mailing list.");
			documentation.put("${mail.subject}", "The mail subject.");		
		}
	}
}

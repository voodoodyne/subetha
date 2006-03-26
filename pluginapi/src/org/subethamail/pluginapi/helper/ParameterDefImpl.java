/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi.helper;

import org.subethamail.pluginapi.ParameterDef;


/**
 * Simple implementation of ParameterDef
 * 
 * @author Jeff Schnitzer
 */
public class ParameterDefImpl implements ParameterDef
{
	String name;
	String description;
	Class type;
	Object defaultValue;
	
	/** */
	public ParameterDefImpl(String name, String description, Class type, Object defaultValue)
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

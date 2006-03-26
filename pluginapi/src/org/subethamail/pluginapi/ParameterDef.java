/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi;


/**
 * The definition of one parameter to a plugin.
 * 
 * @author Jeff Schnitzer
 */
public interface ParameterDef
{
	/**
	 * The short name of this parameter, eg "Max Size in K". This
	 * must be unique among parameters for a particular plugin.
	 */
	public String getName();
	
	/**
	 * The long, verbose description of this parameter and what it does.
	 */
	public String getDescription();
	
	/**
	 * The type of this parameter.  Currently, only the following types
	 * are allowed:
	 * 
	 * java.lang.Boolean (not the primitive)
	 * java.lang.Long (not the primitive)
	 * java.lang.Double (not the primitive)
	 * java.lang.String
	 * any subclass of java.lang.Enum
	 */
	public Class getType();
	
	/**
	 * The default value for this parameter when a plugin is enabled.
	 * The value must be of the type specified by getType().  Null
	 * is an allowable "no default".
	 */
	public Object getDefaultValue();
}

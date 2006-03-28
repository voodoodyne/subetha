/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i;


/**
 * The definition of one parameter to a filter.
 * 
 * @author Jeff Schnitzer
 */
public interface FilterParameter
{
	/**
	 * The short name of this parameter, eg "Max Size in K". This
	 * must be unique among parameters for a particular filter.
	 */
	public String getName();
	
	/**
	 * The long, verbose description of this parameter and what it does.
	 */
	public String getDescription();
	
	/**
	 * <p>The type of this parameter.  You can have any java basic
	 * Object type (ie java.lang.Boolean, not boolean), Strings,
	 * and Enums.  You can also have more complicated classes
	 * as long as they follow the constraints of
	 * @see org.subethamail.entity.type.AnyImmutableType</p>
	 * 
	 * <p>A quick recap:  the object must be immutable, must
	 * implement toString(), must have a static valueOf(String)
	 * method, and must be Serializable.</p>
	 */
	public Class getType();
	
	/**
	 * The default value for this parameter when a plugin is enabled.
	 * The value must be of the type specified by getType().  Null
	 * is an allowable "no default".
	 */
	public Object getDefaultValue();
}

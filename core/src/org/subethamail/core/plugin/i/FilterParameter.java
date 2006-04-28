/*
 * $Id$
 * $URL$
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
	
	/**
	 * If (and only if) type is java.lang.String, this method will
	 * be called to determine the size of the textarea in the
	 * editing UI.  If 1, this will produce a one-line text
	 * input field.  Otherwise it will produce a textarea with
	 * the number of lines specified.
	 */
	public int getTextLines();
	
	/**
	 * Indicates to the UI that the property should be expanded with
	 * velocity macros.  The filter is responsible for calling the
	 * FilterContext.expand() method.  This just causes the UI to
	 * display some helpful text.
	 * 
	 * Only has meaning for java.lang.String parameters.
	 */
	public boolean isExpanded();
}

/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i;


/**
 * Context for filter execution, providing information from the container
 * such as what list is being process and what the filter arguments are.
 * 
 * @author Jeff Schnitzer
 */
public interface FilterContext
{
	/** */
	public String getListAddress();
	
	/** */
	public String getListURL();
	
	/**
	 * @return the correctly-typed value of the named parameter. 
	 */
	public Object getArgument(String name);
}
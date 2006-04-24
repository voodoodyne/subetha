/*
 * $Id$
 * $URL$
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
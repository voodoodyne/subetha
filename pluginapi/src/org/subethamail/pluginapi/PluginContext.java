/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi;


/**
 * Context for plugin execution, providing information from the container
 * such as what list is being process and what the plugin parameters are.
 * 
 * @author Jeff Schnitzer
 */
public interface PluginContext
{
	/** */
	public String getListName();
	
	/** */
	public String getListURL();
	
	/** */
	public Boolean getParamBoolean(String name);
	
	/** */
	public Long getParamLong(String name);
	
	/** */
	public Double getParamDouble(String name);
	
	/** */
	public String getParamString(String name);
	
	/** You must cast it to the actual enum subclass */
	public Enum getParamEnum(String name);
}
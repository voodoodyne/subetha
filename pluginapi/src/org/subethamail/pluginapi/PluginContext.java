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
	public String getListAddress();
	
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
	
	/**
	 * @return the value of the enum.  You have to pass in the
	 *  class of the actual enum type you want. 
	 */
	public <T extends Enum<T>> T getParamEnum(String name, Class<T> enumType);
	
	/**
	 * Called when a plugin wants a message to be held for moderation
	 * by a list administrator or list moderator.  This is only available
	 * during the injection phase; calling it during outbound delivery
	 * will do nothing.
	 */
	public void holdForModeration();
}
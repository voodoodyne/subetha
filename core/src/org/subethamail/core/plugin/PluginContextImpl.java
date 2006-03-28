/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin;

import org.subethamail.entity.EnabledPlugin;
import org.subethamail.entity.PluginParam;
import org.subethamail.pluginapi.PluginContext;


/**
 * Implementation of the PluginContext
 * 
 * @author Jeff Schnitzer
 */
class PluginContextImpl implements PluginContext
{
	/** */
	EnabledPlugin enPlugin;
	PluginFactory factory;
	
	/** 
	 */
	public PluginContextImpl(EnabledPlugin enPlugin, PluginFactory fact)
	{
		this.enPlugin = enPlugin;
		this.factory = fact;
	}
	
	/**
	 * @see PluginContext#getListAddress()
	 */
	public String getListAddress()
	{
		return this.enPlugin.getMailingList().getAddress();
	}

	/**
	 * @see PluginContext#getListURL()
	 */
	public String getListURL()
	{
		return this.enPlugin.getMailingList().getUrl();
	}

	/**
	 * @see PluginContext#getParamBoolean(String)
	 */
	public Boolean getParamBoolean(String name)
	{
		PluginParam param = this.enPlugin.getParams().get(name);

		return (Boolean)param.getValue();
	}

	/**
	 * @see PluginContext#getParamLong(String)
	 */
	public Long getParamLong(String name)
	{
		PluginParam param = this.enPlugin.getParams().get(name);

		return (Long)param.getValue();
	}

	/**
	 * @see PluginContext#getParamDouble(String)
	 */
	public Double getParamDouble(String name)
	{
		PluginParam param = this.enPlugin.getParams().get(name);

		return (Double)param.getValue();
	}

	/**
	 * @see PluginContext#getParamString(String)
	 */
	public String getParamString(String name)
	{
		PluginParam param = this.enPlugin.getParams().get(name);

		return (String)param.getValue();
	}

	/**
	 * @see PluginContext#getParamEnum(String, Class)
	 */
	public <T extends Enum<T>> T getParamEnum(String name, Class<T> enumType)
	{
		// Enums are actually stored as strings
		String value = this.getParamString(name);
		
		return Enum.valueOf(enumType, value);
	}
}
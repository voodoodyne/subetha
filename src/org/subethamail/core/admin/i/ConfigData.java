/*
 * $Id: ConfigData.java 979 2008-10-08 01:14:25Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/ConfigData.java $
 */

package org.subethamail.core.admin.i;

import java.io.Serializable;

/**
 * Detail information about Config
 *
 * @author Jon Stevens
 */
@SuppressWarnings("serial")
public class ConfigData implements Serializable
{
	String id;
	String description;
	Object value;

	/**
	 */
	public ConfigData() {}

	/**
	 */
	public ConfigData(
					String id,
					String description,
					Object value)
	{
		this.id = id;
		this.description = description;
		this.value = value;
	}

	/** */
	@Override
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id +
			", type=" + this.value.getClass().getName() +
			", value=" + this.value + "}";
	}

	/** */
	public String getId()
	{
		return this.id;
	}

	public Object getValue()
	{
		return this.value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public Class<?> getType()
	{
		return this.value.getClass();
	}

	public String getDescription()
	{
		return this.description;
	}

}

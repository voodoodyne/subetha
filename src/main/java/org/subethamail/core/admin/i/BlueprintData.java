package org.subethamail.core.admin.i;

import java.io.Serializable;

/**
 * Detail information about a blueprint, which is a type of
 * list that can be created.
 *
 * @author Jeff Schnitzer
 */
public class BlueprintData implements Serializable
{
	private static final long serialVersionUID = 1L;

	String id;
	String name;
	String description;
	
	/**
	 */
	public BlueprintData() {}
	
	/**
	 */
	public BlueprintData(
					String id, 
					String name,
					String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/** */
	public String getId()
	{
		return this.id;
	}

	/** */
	public String getName()
	{
		return this.name;
	}

	/** */
	public String getDescription()
	{
		return this.description;
	}
}

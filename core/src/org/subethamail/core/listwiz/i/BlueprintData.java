/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.listwiz.i;

import java.io.Serializable;

/**
 * Detail information about a blueprint, which is a type of
 * list that can be created.
 *
 * @author Jeff Schnitzer
 */
public class BlueprintData implements Serializable
{
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

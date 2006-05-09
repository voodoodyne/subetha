/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;

/**
 * Some detail about a mailing list.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class ListData implements Serializable
{
	Long id;
	String email;
	String name;
	String url;
	String description;
	boolean subscriptionHeld;
	
	/**
	 */
	public ListData(Long id, 
					String email,
					String name,
					String url, 
					String description,
					boolean subscriptionHeld)
	{
		this.id = id;
		this.email = email;
		this.name = name;
		this.url = url;
		this.description = description;
		this.subscriptionHeld = subscriptionHeld;
	}
	
	/** */
	public String toString()
	{
		return this.getClass().getName() + " {id=" + this.id + ", name=" + this.name + "}";
	}

	/** */
	public Long getId()
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

	/** */
	public String getEmail()
	{
		return this.email;
	}

	/** */
	public String getUrl()
	{
		return this.url;
	}

	/** */
	public boolean isSubscriptionHeld()
	{
		return this.subscriptionHeld;
	}

}

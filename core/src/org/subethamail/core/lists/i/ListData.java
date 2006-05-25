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
	String urlBase;
	String ownerEmail;
	
	/**
	 */
	public ListData(Long id, 
					String email,
					String name,
					String url,
					String urlBase,
					String description,
					String ownerEmail,
					boolean subscriptionHeld)
	{
		this.id = id;
		this.email = email;
		this.name = name;
		this.url = url;
		this.description = description;
		this.subscriptionHeld = subscriptionHeld;
		this.urlBase = urlBase;
		this.ownerEmail = ownerEmail;
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
	public String getUrlBase()
	{
		return this.urlBase;

	}
	
	public String getOwnerEmail()
	{
		return this.ownerEmail;
	}

	/** */
	public boolean isSubscriptionHeld()
	{
		return this.subscriptionHeld;
	}

}

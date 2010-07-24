/*
 * $Id: ListData.java 979 2008-10-08 01:14:25Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/lists/i/ListData.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;

/**
 * Some detail about a mailing list.
 *
 * @author Jeff Schnitzer
 */
public class ListData implements Serializable, Comparable<ListData>
{
	private static final long serialVersionUID = 1L;

	Long id;
	String email;
	String name;
	String url;
	String description;
	String welcomeMessage;
	boolean subscriptionHeld;
	String urlBase;
	String ownerEmail;

	protected ListData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0
	}

	/**
	 */
	public ListData(Long id,
					String email,
					String name,
					String url,
					String urlBase,
					String description,
					String welcomeMessage,
					String ownerEmail,
					boolean subscriptionHeld)
	{
		this.id = id;
		this.email = email;
		this.name = name;
		this.url = url;
		this.description = description;
		this.welcomeMessage = welcomeMessage;
		this.subscriptionHeld = subscriptionHeld;
		this.urlBase = urlBase;
		this.ownerEmail = ownerEmail;
	}

	/** */
	@Override
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

	public String getWelcomeMessage()
	{
		return this.welcomeMessage;
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

	/** */
	public int compareTo(ListData other)
	{
		// Only return 0 if they are actually identical to make TreeMap happy
		if (this.id.equals(other.id))
			return 0;

		int result = this.name.compareTo(other.getName());
		if (result == 0)
			return this.id.compareTo(other.id);
		else
			return result;
	}

}

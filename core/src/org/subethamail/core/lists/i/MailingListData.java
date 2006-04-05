/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.lists.i;

import java.io.Serializable;

/**
 * Some detail about a mailing list.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class MailingListData implements Serializable
{
	Long id;
	String email;
	String name;
	String url;
	String description;
	
	/**
	 */
	public MailingListData(Long id, 
					String email,
					String name,
					String url, 
					String description)
	{
		this.id = id;
		this.email = email;
		this.name = name;
		this.url = url;
		this.description = description;
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

}

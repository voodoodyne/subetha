/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;

/**
 * Some detail about a person.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class PersonData implements Serializable
{
	protected Long id;
	protected String name;
	protected String[] emailAddresses;
	
	/**
	 */
	public PersonData(Long id, 
					String name,
					String[] emailAddresses)
	{
		this.id = id;
		this.name = name;
		this.emailAddresses = emailAddresses;
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
	public String[] getEmailAddresses()
	{
		return this.emailAddresses;
	}
}

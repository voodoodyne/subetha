/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;

/**
 * Some detail about a person.
 *
 * @author Jeff Schnitzer
 */
public class PersonData implements Serializable
{
	Long id;
	String name;
	String[] emailAddresses;
	
	/**
	 */
	public PersonData() {}
	
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

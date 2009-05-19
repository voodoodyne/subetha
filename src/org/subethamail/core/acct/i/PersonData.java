/*
 * $Id: PersonData.java 963 2007-07-04 01:05:05Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/acct/i/PersonData.java $
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;
import java.util.List;

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
	protected List<String> emailAddresses;
	protected boolean isSiteAdmin = false;
	
	protected PersonData()
	{
		// http://forums.java.net/jive/thread.jspa?threadID=26539&tstart=0		
	}

	public PersonData(Long id, String name, List<String> emailAddresses)
	{
		this.id = id;
		this.name = name;
		this.emailAddresses = emailAddresses;
	}

	/**
	 */
	public PersonData(Long id, 
					String name,
					List<String> emailAddresses,
					boolean isSiteAdmin)
	{
		this.id = id;
		this.name = name;
		this.emailAddresses = emailAddresses;
		this.isSiteAdmin = isSiteAdmin;
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
	public List<String> getEmailAddresses()
	{
		return this.emailAddresses;
	}

	public boolean isSiteAdmin()
	{
		return this.isSiteAdmin;
	}

	public void setSiteAdmin(boolean isSiteAdmin)
	{
		this.isSiteAdmin = isSiteAdmin;
	}
}

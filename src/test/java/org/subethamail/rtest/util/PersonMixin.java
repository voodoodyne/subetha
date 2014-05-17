/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;


/**
 * Actually creates the person.
 * 
 * @author Jeff Schnitzer
 */
public class PersonMixin extends PersonInfoMixin
{
	Long id;
	
	/** */
	public PersonMixin(AdminMixin adminMixin) throws Exception
	{
		this.id = adminMixin.getAdmin().establishPerson(this.address, this.password);
	}
	
	/** */
	public Long getId() { return this.id; }
	
	/** */
	@Override
	public String getPrincipalName() { return this.getEmail(); }
	
}

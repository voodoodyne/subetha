/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actually creates the person.
 * 
 * @author Jeff Schnitzer
 */
public class PersonMixin extends PersonInfoMixin
{
	/** */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PersonMixin.class);

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

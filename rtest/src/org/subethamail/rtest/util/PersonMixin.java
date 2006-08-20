/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Actually creates the person.
 * 
 * @author Jeff Schnitzer
 */
public class PersonMixin extends PersonInfoMixin
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(PersonMixin.class);

	Long id;
	
	/** */
	public PersonMixin(AdminMixin adminMixin) throws Exception
	{
		super();
		
		this.id = adminMixin.getAdmin().establishPerson(this.address, this.password);
	}
	
	/** */
	public Long getId() { return this.id; }
	
	/** */
	@Override
	public String getPrincipalName() { return this.id.toString(); }
	
}

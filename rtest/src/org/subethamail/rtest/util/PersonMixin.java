/*
 * $Id: PersonMixin.java 88 2006-02-22 13:51:08Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/PersonMixin.java $
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
}

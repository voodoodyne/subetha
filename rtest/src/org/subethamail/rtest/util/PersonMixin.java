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

	/** */
	public PersonMixin(AdminMixin adminMixin) throws Exception
	{
		super();
	}
	
	/** */
	@Override
	public String getPrincipalName() { return email;}
	
}

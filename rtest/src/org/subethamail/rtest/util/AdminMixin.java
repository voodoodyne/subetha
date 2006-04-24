/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Using this mixin allows accessing ejbs from the siteAdmin
 * role.  You must have a siteAdmin account "root@localhost",
 * password "password".
 * 
 * @author Jeff Schnitzer
 */
public class AdminMixin extends BeanMixin
{
	/** */
	private static Log log = LogFactory.getLog(AdminMixin.class);

	/** */
	public AdminMixin() throws Exception
	{}
	
	@Override
	public String getEmail() { return "root@localhost"; }
	
	@Override
	public String getPassword() { return "password"; }
	
}

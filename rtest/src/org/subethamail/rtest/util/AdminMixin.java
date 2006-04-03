/*
 * $Id: AdminMixin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/AdminMixin.java $
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

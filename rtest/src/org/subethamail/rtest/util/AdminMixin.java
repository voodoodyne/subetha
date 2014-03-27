/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;


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
	public static final String EMAIL = "root@localhost";
	public static final String PASSWORD = "password";
	
	/** */
	public AdminMixin() throws Exception
	{
	}
	
	@Override
	public String getPrincipalName() { return EMAIL; }
	
	@Override
	public String getPassword() { return PASSWORD; }
	
}

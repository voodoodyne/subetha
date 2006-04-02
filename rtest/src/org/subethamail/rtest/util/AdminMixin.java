/*
 * $Id: AdminMixin.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/util/AdminMixin.java $
 */

package org.subethamail.rtest.util;

import java.security.Principal;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;

/**
 * This class makes it easy to obtain and use the secured 
 * administration interfaces from unit tests.  Every time
 * getAdmin() is called, it resets the security credentials
 * to a known administrative user.
 * 
 * You must have a siteAdmin account "root@localhost", password
 * "password".
 * 
 * @author Jeff Schnitzer
 */
public class AdminMixin
{
	/** */
	private static Log log = LogFactory.getLog(AdminMixin.class);

	public static final String EMAIL = "root@localhost";
	public static final String PASSWORD = "password";
	
	private Admin admin;
	
	/** */
	public AdminMixin() throws Exception
	{
		Context ctx = new InitialContext();
		this.admin = (Admin)ctx.lookup(AdminRemote.JNDI_NAME);
	}
	
	/**
	 * Establish administrator credentials
	 */
	public void establish()
	{
		Principal p = new SimplePrincipal(EMAIL);
        SecurityAssociation.setPrincipal(p);
        SecurityAssociation.setCredential(PASSWORD);
	}
	
	/**
	 * Get rid of all credentials
	 */
	public void clearCredentials()
	{
        SecurityAssociation.setPrincipal(null);
        SecurityAssociation.setCredential(null);
	}
	
	/** */
	public Admin getAdmin() throws Exception
	{
		this.establish();
		
		return this.admin;
	}
}

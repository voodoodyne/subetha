/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import java.net.URL;

import javax.mail.internet.InternetAddress;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.Utils;
import org.subethamail.core.admin.i.SiteStatus;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * Tests for server administration functions
 * 
 * @author Jeff Schnitzer
 */
public class ServerAdminTest extends SubEthaTestCase
{
	/** */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ServerAdminTest.class);

	/** */
	AdminMixin admin;
	
	/** */
	public ServerAdminTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
	}
	
	/** */
	public void testSetDefaultSiteUrl() throws Exception
	{
		String uniqueString = Utils.uniqueString();
		URL newUrl = new URL("http://" + uniqueString + "/");
		
		this.admin.getAdmin().setDefaultSiteUrl(newUrl.toString());
		
		SiteStatus stat = this.admin.getAdmin().getSiteStatus();
		
		assertEquals(newUrl, stat.getDefaultSiteUrl());
	}
	
	/** */
	public void testSetPostmaster() throws Exception
	{
		String email = Utils.uniqueString() + "@localhost";
		String name = Utils.uniqueString();
		InternetAddress addy = new InternetAddress(email, name);

		// Change both parts
		this.admin.getAdmin().setPostmaster(addy);
		
		SiteStatus stat = this.admin.getAdmin().getSiteStatus();
		
		assertEquals(email, stat.getPostmasterEmail().getAddress());
		assertEquals(name, stat.getPostmasterEmail().getPersonal());
		
		// Now change just personal part
		name = Utils.uniqueString();
		addy = new InternetAddress(email, name);
		
		this.admin.getAdmin().setPostmaster(addy);
		
		stat = this.admin.getAdmin().getSiteStatus();
		
		assertEquals(email, stat.getPostmasterEmail().getAddress());
		assertEquals(name, stat.getPostmasterEmail().getPersonal());
		
		// Now change just the email part
		email = Utils.uniqueString();
		addy = new InternetAddress(email, name);
		
		this.admin.getAdmin().setPostmaster(addy);
		
		stat = this.admin.getAdmin().getSiteStatus();
		
		assertEquals(email, stat.getPostmasterEmail().getAddress());
		assertEquals(name, stat.getPostmasterEmail().getPersonal());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(ServerAdminTest.class);
	}
}

/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.Self;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.Smtp;

import com.dumbster.smtp.SimpleSmtpServer;

/**
 * Tests for user account manipulation.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAddressTest extends TestCase
{
	/** */
	private static Log log = LogFactory.getLog(EmailAddressTest.class);

	/** */
	AdminMixin admin;
	PersonMixin pers;
	
	/** */
	public EmailAddressTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
	}
	
	/** */
	public void testAddEmail() throws Exception
	{
		SimpleSmtpServer server = Smtp.start();
		
		PersonInfoMixin additional = new PersonInfoMixin();
		this.pers.getAccountMgr().requestAddEmail(additional.getEmail());

		server.stop();
		
		assertEquals(1, server.getReceivedEmailSize());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(EmailAddressTest.class);
	}
}

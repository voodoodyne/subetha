/*
 * $Id: EmailAddressTest.java 1105 2009-05-10 04:16:03Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/rtest/src/org/subethamail/rtest/EmailAddressTest.java $
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.SubEthaTestCase;
import org.subethamail.wiser.Wiser;

/**
 * This is some useful code to have around from when testing the fallthrough
 * mechanism by hand.  Doesn't end in Test therefore shouldn't be run by
 * the test harness.  Rename it in the future to be useful.
 * 
 * @author Jeff Schnitzer
 */
public class FallthroughByHand extends SubEthaTestCase
{
	/** */
	public static final int FALLTHROUGH_PORT = 2526;

	/** */
	Wiser fallthrough;
	Wiser smtp;
	AdminMixin admin;
	
	/** */
	@Before
	public void setUp() throws Exception
	{
		this.fallthrough = new Wiser(FALLTHROUGH_PORT);
		this.fallthrough.start();
		
		this.smtp = new Wiser(2525);
		this.smtp.start();
		
		this.admin = new AdminMixin();
		
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "localhost");
		props.setProperty("mail.smtp.port", "2500");
		this.sess = Session.getDefaultInstance(props);
		
		//this.admin.getEegor().enableTestMode("localhost:2525");
	}
	
	/** */
	@After
	public void tearDown() throws Exception
	{
		this.fallthrough.stop();
		this.smtp.stop();
	}
	
	/** */
    @org.junit.Test
	public void testListOnly() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		MimeMessage msg = this.createMimeMessage(new InternetAddress("source@localhost"), new InternetAddress("test@localhost"));
		Transport.send(msg);
		Thread.sleep(1000);

		assertEquals(0, this.fallthrough.getMessages().size());
		assertEquals(1, this.smtp.getMessages().size());
	}

	/** */
    @org.junit.Test
	public void testFallthroughPlusList() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		MimeMessage msg = this.createMimeMessage(new InternetAddress("source@localhost"), new InternetAddress("test@localhost"));
		msg.addRecipient(RecipientType.TO, new InternetAddress("doesnotexist@localhost"));
		Transport.send(msg);
		Thread.sleep(1000);

		assertEquals(1, this.fallthrough.getMessages().size());
		assertEquals(1, this.smtp.getMessages().size());
	}
	
}

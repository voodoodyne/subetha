/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest.util;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * All SubEtha tests require a running smtp server.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaTestCase extends TestCase
{
	/** */
	private static Log log = LogFactory.getLog(SubEthaTestCase.class);

	/** */
	public static final String TEST_SUBJECT = "test subject";
	public static final String TEST_BODY = "test body";

	/** */
	protected Smtp smtp;
	protected Session sess;
	
	/** */
	public SubEthaTestCase(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.smtp = Smtp.start();
		this.sess = Session.getDefaultInstance(new Properties());
		
	}
	
	/** */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		
		this.smtp.stop();
		this.smtp = null;
		
		this.sess = null;
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SubEthaTestCase.class);
	}
	
	/**
	 * 
	 */
	protected byte[] createMessage(InternetAddress from, InternetAddress to)
	{
		MimeMessage msg = new MimeMessage(this.sess);
		
		msg.setFrom(from);
		msg.setRecipient(Message.RecipientType.TO, to);
		msg.setSubject(TEST_SUBJECT);
		msg.setText(TEST_BODY);
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		msg.writeTo(buf);
		
		return buf.toByteArray();
	}
}

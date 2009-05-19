/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All SubEtha tests require a running smtp server.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaTestCase extends TestCase
{
	/** */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(SubEthaTestCase.class);

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
		
		this.smtp = new Smtp();
		this.smtp.start();
		this.sess = Session.getDefaultInstance(new Properties());		
	}
	
	/** */
	protected void tearDown() throws Exception
	{
		this.smtp.stop();
		this.smtp = null;
		
		this.sess = null;
		
		super.tearDown();
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SubEthaTestCase.class);
	}
	
	/**
	 * Create the bytes of a simple test message
	 */
	protected byte[] createMessage(InternetAddress from, InternetAddress to) throws MessagingException, IOException
	{
		return this.createMessage(from, to, TEST_SUBJECT, TEST_BODY);
	}
	
	/**
	 * Create the bytes of a slightly more complicated test message
	 */
	protected byte[] createMessage(InternetAddress from, InternetAddress to, String subject, String body) throws MessagingException, IOException
	{
		MimeMessage msg = new MimeMessage(this.sess);
		
		msg.setFrom(from);
		msg.setRecipient(Message.RecipientType.TO, to);
		msg.setSubject(subject);
		msg.setText(body);
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		msg.writeTo(buf);
		
		return buf.toByteArray();
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
		
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "localhost");
		props.setProperty("mail.smtp.port", "2500");
		this.sess = Session.getDefaultInstance(props);		
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
	protected MimeMessage createMimeMessage(InternetAddress from, InternetAddress to) throws MessagingException, IOException
	{
		return this.createMimeMessage(from, to, TEST_SUBJECT, TEST_BODY);
	}
	
	/**
	 * Create the bytes of a slightly more complicated test message
	 */
	protected MimeMessage createMimeMessage(InternetAddress from, InternetAddress to, String subject, String body) throws MessagingException, IOException
	{
		MimeMessage msg = new MimeMessage(this.sess);
		
		msg.setFrom(from);
		msg.setRecipient(Message.RecipientType.TO, to);
		msg.setSubject(subject);
		msg.setText(body);
		
		return msg;
	}
	
	/**
	 * Create the bytes of a slightly more complicated test message
	 */
	protected byte[] createMessage(InternetAddress from, InternetAddress to, String subject, String body) throws MessagingException, IOException
	{
		MimeMessage msg = this.createMimeMessage(from, to, subject, body);
		
		return this.byteify(msg);
	}
	
	/**
	 */
	protected byte[] byteify(MimeMessage msg) throws MessagingException, IOException
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		msg.writeTo(buf);
		
		return buf.toByteArray();
	}

	/**
	 * Create the bytes of a test message from a file.  The filename should not have
	 * path info attached, that will get added here.
	 */
	protected byte[] createMessageFromFile(String filename) throws MessagingException, IOException
	{
		File f = new File("rtest/testdata", filename);
		
		byte[] result = new byte[(int)f.length()];
		
		FileInputStream in = new FileInputStream(f);
		int count = in.read(result);

		assert count == result.length;
		
		return result;
	}
	
}

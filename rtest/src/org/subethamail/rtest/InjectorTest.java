/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class InjectorTest extends SubEthaTestCase
{
	/** */
	private static Log log = LogFactory.getLog(InjectorTest.class);
	
	/** */
	public static final String TEST_SUBJECT = "test subject";
	public static final String TEST_BODY = "test body";

	/** */
	Injector injector;
	Session sess;
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin person1;
	PersonMixin person2;
	
	/** */
	public InjectorTest(String name) { super(name); }
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		Context ctx = new InitialContext();
		
		this.injector = (Injector)ctx.lookup(InjectorRemote.JNDI_NAME);
		
		this.sess = Session.getDefaultInstance(new Properties());
		
		this.admin = new AdminMixin();
		this.person1 = new PersonMixin(this.admin);
		this.person2 = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, null);
		
		this.person1.getAccountMgr().subscribeMe(this.ml.getId(), this.person1.getEmail());
		this.person2.getAccountMgr().subscribeMe(this.ml.getId(), this.person2.getEmail());
	}
	
	/** */
	public void testTrivialInjection() throws Exception
	{
		// Two "you are subscribed" msgs
		assertEquals(2, this.smtp.size());
		
		MimeMessage msg = new MimeMessage(this.sess);
		
		msg.setFrom(this.person1.getAddress());
		msg.setRecipient(Message.RecipientType.TO, this.ml.getAddress());
		msg.setSubject(TEST_SUBJECT);
		msg.setText(TEST_BODY);
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		msg.writeTo(buf);
		
		this.injector.inject(this.ml.getEmail(), buf.toByteArray());
		
		assertEquals(2, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(InjectorTest.class);
	}
}

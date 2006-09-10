/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;
import org.subethamail.wiser.WiserMessage;

/**
 * @author Jeff Schnitzer
 */
public class InjectorTest extends SubEthaTestCase
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(InjectorTest.class);
	
	/** */
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin person1;
	
	/** */
	public InjectorTest(String name) { super(name); }
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.person1 = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, null);
		
		this.person1.getAccountMgr().subscribeMe(this.ml.getId(), this.person1.getEmail());
	}
	
	/** */
	public void testTrivialInjection() throws Exception
	{
		// Create a second subscriber to make it more interesting
		PersonMixin person2 = new PersonMixin(this.admin);
		person2.getAccountMgr().subscribeMe(this.ml.getId(), person2.getEmail());
		
		this.admin.getAdmin().log("############### Starting testTrivialInjection()");
		
		this.smtp.debugPrintSubjects();
		
		// Two "you are subscribed" msgs
		assertEquals(2, this.smtp.count(MailType.YOU_SUBSCRIBED));
		
		byte[] rawMsg = this.createMessage(this.person1.getAddress(), this.ml.getAddress());
		
		this.admin.getInjector().inject(this.person1.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		Thread.sleep(1000);
		
		assertEquals(2, this.smtp.countSubject(TEST_SUBJECT));
		
		this.admin.getAdmin().log("############### Ended testTrivialInjection()");
	}
	
	/** */
	public void testHasVERP() throws Exception
	{
		this.admin.getAdmin().log("############### Starting testHasVERP()");
		
		// Two "you are subscribed" msg
		assertEquals(1, this.smtp.count(MailType.YOU_SUBSCRIBED));
		
		byte[] rawMsg = this.createMessage(this.person1.getAddress(), this.ml.getAddress());
		
		this.admin.getInjector().inject(this.person1.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		Thread.sleep(1000);
		
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
		WiserMessage msg = this.smtp.getSubject(TEST_SUBJECT, 0);
		
		String envelopeSender = msg.getEnvelopeSender();
		assertTrue(envelopeSender.contains("-verp-"));
		
		this.admin.getAdmin().log("############### Ended testHasVERP()");
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(InjectorTest.class);
	}
}

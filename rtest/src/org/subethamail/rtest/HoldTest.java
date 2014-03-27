/*
 * $Id: ModerationTest.java 1075 2009-05-07 06:41:19Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/rtest/src/org/subethamail/rtest/ModerationTest.java $
 */

package org.subethamail.rtest;

import javax.mail.internet.MimeMessage;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class HoldTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin pers1;
	PersonInfoMixin pers2;
	
	/** */
	public HoldTest(String name) { super(name); }
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers1 = new PersonMixin(this.admin);
		this.pers2 = new PersonInfoMixin();
		
		// This one moderates inbound messages but allows anyone to subscribe
		this.ml = new MailingListMixin(this.admin, null, "org.subethamail.plugin.blueprint.TechnicalBlueprint");
		
		// Pers is subscribed
		this.pers1.getAccountMgr().subscribeMe(this.ml.getId(), this.pers1.getEmail());
	}
	
	/** */
	public void testSenderField() throws Exception
	{
		MimeMessage msg = this.createMimeMessage(this.pers2.getAddress(), this.ml.getAddress(), TEST_SUBJECT, TEST_BODY);
		msg.setSender(this.pers1.getAddress());
		
		byte[] rawMsg = this.byteify(msg);

		// Pers2 is not subscribed but there was a sender field for pers1
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
	public void testEnvelopeSender() throws Exception
	{
		byte[] rawMsg = this.createMessage(this.pers2.getAddress(), this.ml.getAddress());
		
		// Pers2 is not subscribed but envelope sender is pers1
		this.admin.getInjector().inject(this.pers1.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(HoldTest.class);
	}
}

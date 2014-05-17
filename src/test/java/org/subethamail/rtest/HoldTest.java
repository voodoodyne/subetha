/*
 * $Id: ModerationTest.java 1075 2009-05-07 06:41:19Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/rtest/src/org/subethamail/rtest/ModerationTest.java $
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;

import javax.mail.internet.MimeMessage;

import org.junit.Assume;
import org.junit.Before;
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

	/**
	 * Creates a mailing list with two people subscribed.
	 */
	@Before
	public void setUp() throws Exception
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
    @org.junit.Test
	public void testSenderField() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		MimeMessage msg = this.createMimeMessage(this.pers2.getAddress(), this.ml.getAddress(), TEST_SUBJECT, TEST_BODY);
		msg.setSender(this.pers1.getAddress());
		
		byte[] rawMsg = this.byteify(msg);

		// Pers2 is not subscribed but there was a sender field for pers1
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
    @org.junit.Test
	public void testEnvelopeSender() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		byte[] rawMsg = this.createMessage(this.pers2.getAddress(), this.ml.getAddress());
		
		// Pers2 is not subscribed but envelope sender is pers1
		this.admin.getInjector().inject(this.pers1.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
}

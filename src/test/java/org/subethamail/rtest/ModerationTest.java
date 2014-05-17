/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.core.lists.i.MailHold;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.Smtp;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class ModerationTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin pers;
	PersonInfoMixin pers2;
	byte[] rawMsg;
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.pers2 = new PersonInfoMixin();
		
		// This one moderates inbound messages but allows anyone to subscribe
		this.ml = new MailingListMixin(this.admin, null, "org.subethamail.plugin.blueprint.TechnicalBlueprint");
		
		// Pers is subscribed
		this.pers.getAccountMgr().subscribeMe(this.ml.getId(), this.pers.getEmail());
		
		this.rawMsg = this.createMessage(this.pers2.getAddress(), this.ml.getAddress());
	}
	
	/** */
    @org.junit.Test
	public void testHeldMailNotification() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		// Pers2 is not subscribed
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.count(MailType.YOUR_MAIL_HELD));
		
		// A second try should not produce a held notification
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.count(MailType.YOUR_MAIL_HELD));
	}
	
	/** */
    @org.junit.Test
	public void testDiscard() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		assertEquals(1, this.smtp.count(MailType.YOUR_MAIL_HELD));
		
		Collection<MailHold> msgs = this.admin.getListMgr().getHeldMessages(this.ml.getId(), -1, -1);
		assertEquals(1, msgs.size());
		MailHold msg = msgs.iterator().next();
		assertFalse(msg.isHard());
		assertEquals(this.pers2.getAddress().toString(), msg.getFrom());
		
		this.admin.getListMgr().discardHeldMessage(msg.getId());
		
		msgs = this.admin.getListMgr().getHeldMessages(this.ml.getId(), -1, -1);
		assertEquals(0, msgs.size());
	}
	
	/** */
    @org.junit.Test
	public void testManualApproval() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		Collection<MailHold> msgs = this.admin.getListMgr().getHeldMessages(this.ml.getId(), -1, -1);
		MailHold msg = msgs.iterator().next();
		
		this.admin.getListMgr().approveHeldMessage(msg.getId());
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
    @org.junit.Test
	public void testSelfApproval() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getInjector().inject(this.pers2.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		this.pers.getAccountMgr().addEmailRequest(this.pers2.getEmail());
		assertEquals(1, this.smtp.count(MailType.CONFIRM_EMAIL));
		
		String token = Smtp.extractToken(this.smtp.get(MailType.CONFIRM_EMAIL, 0));
		
		this.pers.getAccountMgr().addEmail(token);
		
		Thread.sleep(1000);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
}

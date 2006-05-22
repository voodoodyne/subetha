/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.Smtp;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * Tests for user account manipulation.
 * 
 * @author Jeff Schnitzer
 */
public class EmailAddressTest extends SubEthaTestCase
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
		PersonInfoMixin additional = new PersonInfoMixin();
		this.pers.getAccountMgr().addEmailRequest(additional.getEmail());

		// Should have a CONFIRM_EMAIL msg
		assertEquals(1, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.CONFIRM_EMAIL));
		
		String token = Smtp.extractToken(this.smtp.get(0));
		
		this.pers.getAccountMgr().addEmail(token);
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEmailInSelf(additional.getEmail(), me);
	}
	
	/** */
	public void testMergeAccounts() throws Exception
	{
		PersonMixin additional = new PersonMixin(this.admin);
		
		this.pers.getAccountMgr().addEmailRequest(additional.getEmail());

		// Should have a CONFIRM_EMAIL msg
		assertEquals(1, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.CONFIRM_EMAIL));
		
		String token = Smtp.extractToken(this.smtp.get(0));
		
		this.pers.getAccountMgr().addEmail(token);
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEmailInSelf(additional.getEmail(), me);
		
		try
		{
			this.admin.getAdmin().log("##### the getSelf() should fail");
			
			me = additional.getAccountMgr().getSelf();
			fail("Accessed an account that should have been deleted");
		}
		catch (Exception ex) {}
	}
	
	/** */
	public void testMergeSingleSubscription() throws Exception
	{
		// Subfixture
		PersonMixin additional = new PersonMixin(this.admin);
		MailingListMixin list = new MailingListMixin(this.admin, additional.getAddress());
		
		this.admin.getAdmin().addEmail(this.pers.getId(), additional.getEmail());
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEquals(this.pers.getId(), me.getId());
		assertEquals(1, me.getSubscriptions().size());
		assertEquals(list.getId(), me.getSubscriptions().get(0).getId());
	}
	
	/** */
	public void testMergeSingleOverlappingSubscription() throws Exception
	{
		// Subfixture
		PersonMixin additional = new PersonMixin(this.admin);
		MailingListMixin list = new MailingListMixin(this.admin, additional.getAddress());
		this.admin.getAdmin().subscribe(list.getId(), this.pers.getId(), this.pers.getEmail(), true);
		
		this.admin.getAdmin().addEmail(this.pers.getId(), additional.getEmail());
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEquals(this.pers.getId(), me.getId());
		assertEquals(1, me.getSubscriptions().size());
		assertEquals(list.getId(), me.getSubscriptions().get(0).getId());
	}
	
	/** */
	public void testMergeIntoSelf() throws Exception
	{
		MailingListMixin list = new MailingListMixin(this.admin, this.pers.getAddress());
		
		this.admin.getAdmin().addEmail(this.pers.getId(), this.pers.getEmail());
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEquals(this.pers.getId(), me.getId());
		assertEquals(1, me.getSubscriptions().size());
		assertEquals(list.getId(), me.getSubscriptions().get(0).getId());
	}
	
	/** */
	protected void assertEmailInSelf(String email, Self me)
	{
		for (String selfEmail: me.getEmailAddresses())
		{
			if (selfEmail.equals(email))
				return;
		}
		
		fail("Missing " + email + " from " + me);
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(EmailAddressTest.class);
	}
}

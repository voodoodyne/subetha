/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.core.acct.i.Self;
import org.subethamail.core.post.i.MailType;
import org.subethamail.core.util.VERPAddress;
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
	AdminMixin admin;
	PersonMixin pers;
	
	/** */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
	}
	
	/** */
    @org.junit.Test
	public void testAddEmail() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
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
    @org.junit.Test
	public void testMergeAccounts() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
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
    @org.junit.Test
	public void testMergeSingleSubscription() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
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
    @org.junit.Test
	public void testMergeSingleOverlappingSubscription() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
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
    @org.junit.Test
	public void testMergeIntoSelf() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		MailingListMixin list = new MailingListMixin(this.admin, this.pers.getAddress());
		
		this.admin.getAdmin().addEmail(this.pers.getId(), this.pers.getEmail());
		
		Self me = this.pers.getAccountMgr().getSelf();
		
		assertEquals(this.pers.getId(), me.getId());
		assertEquals(1, me.getSubscriptions().size());
		assertEquals(list.getId(), me.getSubscriptions().get(0).getId());
	}
	
	/** */
    @org.junit.Test
	public void testVERPAddressParser() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		String verpAddress = "smtp-verp-ABCD-b@subethamail.org";
		VERPAddress addr = VERPAddress.getVERPBounce(verpAddress);
		assertEquals("smtp@subethamail.org", addr.getEmail());
		assertEquals("ABCD", addr.getRawToken());
	}

	/** */
	protected void assertEmailInSelf(String email, Self me)
	{
		if (me.getEmailAddresses().contains(email)) return;

		fail("Missing " + email + " from " + me);
	}
}

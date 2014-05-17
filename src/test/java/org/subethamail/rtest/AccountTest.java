/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.core.acct.i.Self;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * Tests for user account manipulation.
 * 
 * @author Jeff Schnitzer
 */
public class AccountTest extends SubEthaTestCase
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
	public void testGetSelf() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		Self self = this.pers.getAccountMgr().getSelf();
		assertEquals(this.pers.getId(), self.getId());
		assertEquals(this.pers.getName(), self.getName());
		assertEquals(1, self.getEmailAddresses().size());
		assertEquals(this.pers.getEmail(), self.getEmailAddresses().get(0));
		assertFalse(self.isSiteAdmin());
	}
	
	/** */
    @org.junit.Test
	public void testSetAdmin() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		Self self = this.pers.getAccountMgr().getSelf();
		assertFalse(self.isSiteAdmin());
		
		this.admin.getAdmin().setSiteAdmin(this.pers.getId(), true);
		
		self = this.pers.getAccountMgr().getSelf();
		assertTrue(self.isSiteAdmin());
	}
	
}

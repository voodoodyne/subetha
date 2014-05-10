/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class BlueprintsTest extends SubEthaTestCase
{
	/** */
	Injector injector;
	AdminMixin admin;
	PersonMixin pers;
	PersonInfoMixin pers2;
	
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
		
		this.injector = this.admin.getInjector();
	}
	
	/** */
    @org.junit.Test
	public void testFreeForAll() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress(), "org.subethamail.plugin.blueprint.FreeForAllBlueprint");
		byte[] rawMsg = this.createMessage(this.pers2.getAddress(), ml.getAddress());
		
		this.injector.inject(this.pers2.getAddress().getAddress(), ml.getEmail(), rawMsg);
		
		Thread.sleep(500);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
}

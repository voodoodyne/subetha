/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BlueprintsTest.class);
	
	/** */
	Injector injector;
	AdminMixin admin;
	PersonMixin pers;
	PersonInfoMixin pers2;
	
	/** */
	public BlueprintsTest(String name) { super(name); }
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.pers2 = new PersonInfoMixin();
		
		this.injector = this.admin.getInjector();
	}
	
	/** */
	public void testFreeForAll() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress(), "org.subethamail.plugin.blueprint.FreeForAllBlueprint");
		byte[] rawMsg = this.createMessage(this.pers2.getAddress(), ml.getAddress());
		
		this.injector.inject(this.pers2.getAddress().getAddress(), ml.getEmail(), rawMsg);
		
		Thread.sleep(500);
		assertEquals(1, this.smtp.countSubject(TEST_SUBJECT));
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(BlueprintsTest.class);
	}
}

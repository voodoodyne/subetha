/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.NobodyMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * Tests for maipulating mailing lists.
 * 
 * @author Jeff Schnitzer
 */
public class MailingListTest extends SubEthaTestCase
{
	/** */
	private static Log log = LogFactory.getLog(MailingListTest.class);

	/** */
	AdminMixin admin;
	PersonMixin pers;
	NobodyMixin nobody;
	
	/** */
	public MailingListTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.nobody = new NobodyMixin();
	}
	
	/** */
	public void testCreateMailingListForExistingPerson() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers);

		// Should contain a "Your new list" email
		assertEquals(1, this.smtp.size());
	}
	
	/** */
	public void testCreateMailingListForNewPerson() throws Exception
	{
		PersonInfoMixin info = new PersonInfoMixin();
		MailingListMixin ml = new MailingListMixin(this.admin, info);

		// Should contain a "Your new account" email
		// Should contain a "Your new list" email
		assertEquals(2, this.smtp.size());
	}
	
	/** */
	public void testSubscribeNewPersonToList() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers);
		
		PersonInfoMixin info = new PersonInfoMixin();
		
		this.nobody.getReceptionist().requestSubscription(info.getEmail(), ml.getId(), info.getName());
		
		// Should contain a "Your new account" email
		// Should contain a "Your new list" email
		assertEquals(2, this.smtp.size());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(MailingListTest.class);
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.BeanMixin;
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
public class SubscriptionTest extends SubEthaTestCase
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SubscriptionTest.class);

	/** */
	BeanMixin nobody;
	AdminMixin admin;
	PersonMixin pers;
	MailingListMixin ml;
	
	/** */
	public SubscriptionTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.nobody = new BeanMixin();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, this.pers.getAddress());
	}
	
	/** */
	public void testSubscribeNewPerson() throws Exception
	{
		// From the list creation
		assertEquals(1, this.smtp.size());
		
		PersonInfoMixin info = new PersonInfoMixin();
		
		this.nobody.getAccountMgr().subscribeAnonymousRequest(ml.getId(), info.getEmail(), info.getName());
		
		// Should contain a "Confirm subscription" email
		assertEquals(2, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.CONFIRM_SUBSCRIBE));
		
		String token = Smtp.extractToken(this.smtp.get(1));
		
		AuthSubscribeResult result = this.nobody.getAccountMgr().subscribeAnonymous(token);
		
		assertEquals(SubscribeResult.OK, result.getResult());
		
		// Now should also contain a "you are subscribed" email
		assertEquals(3, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.YOU_SUBSCRIBED));
	}
	
	/** */
	public void testSubscribeExistingPerson() throws Exception
	{
		// From the list creation
		assertEquals(1, this.smtp.size());
		
		PersonMixin someone = new PersonMixin(this.admin);
		
		SubscribeResult result = someone.getAccountMgr().subscribeMe(ml.getId(), someone.getEmail());
		
		assertEquals(SubscribeResult.OK, result);
		
		// Should contain a "you are subscribed" email
		assertEquals(2, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.YOU_SUBSCRIBED));
	}
	
	/** */
	public void testChangeDeliveryAddress() throws Exception
	{
		PersonMixin someone = new PersonMixin(this.admin);
		
		SubscribeResult result = someone.getAccountMgr().subscribeMe(ml.getId(), someone.getEmail());
		
		assertEquals(SubscribeResult.OK, result);
		
		result = someone.getAccountMgr().subscribeMe(ml.getId(), null);
		
		assertEquals(SubscribeResult.OK, result);
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SubscriptionTest.class);
	}
}

/*
 * $Id: AccountTest.java 1075 2009-05-07 06:41:19Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/rtest/src/org/subethamail/rtest/AccountTest.java $
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;
import lombok.extern.java.Log;

import org.junit.Assume;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.Smtp;

/**
 * Very basic tests that should be run first
 * 
 * @author Jeff Schnitzer
 */
@Log
public class AAATest
{	
	/** */
    @org.junit.Test
	public void testFirstThing() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		log.info("Starting first test");
		AdminMixin admin = new AdminMixin();
		admin.getEegor().log("############# FIRST TEST RUN SUCCESSFULLY");
	}
	
	/** */
    @org.junit.Test
	public void testSecondThing() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		log.info("Starting second test");
		Smtp smtp = new Smtp();
		smtp.start();
		
		AdminMixin admin = new AdminMixin();
		admin.getAdmin().log("############# SECOND TEST RUNNING");
		
		PersonMixin pers = new PersonMixin(admin);
		MailingListMixin ml = new MailingListMixin(admin, null);
		
		log.info("Executing subscribe");
		pers.getAccountMgr().subscribeMe(ml.getId(), pers.getEmail());
		assertEquals(1, smtp.count(MailType.YOU_SUBSCRIBED));
		
		smtp.stop();
	}	
}

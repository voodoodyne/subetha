/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import static org.junit.Assert.fail;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.common.NotFoundException;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class ArchiveTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	PersonMixin pers;
	MailingListMixin ml;
	
	/** */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		
		// Create a list with the person subscribed
		this.pers = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, this.pers.getAddress());
	}
	
	/** */
	@org.junit.Test
	public void testDeleteMessage() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		// Create two messages, one with subject one with body
		byte[] rawMsg = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), TEST_SUBJECT, TEST_BODY);
		
		this.admin.getEegor().log("### Calling Injector.inject()");
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		this.admin.getEegor().log("### Calling Archiver.getThreads()");
		Long mailId = this.admin.getArchiver().getThreads(this.ml.getId(), 0, 100).get(0).getId();
		
		this.admin.getEegor().log("### Calling Archiver.deleteMail()");
		this.admin.getArchiver().deleteMail(mailId);
		
		try
		{
			this.admin.getEegor().log("### Calling Archiver.getMail()");
			this.admin.getArchiver().getMail(mailId);
			fail("able to get deleted mail");
		}
		catch (NotFoundException ex) {}
		
		// TODO:  test when mail has replies or is a reply to other mail
	}
	
}

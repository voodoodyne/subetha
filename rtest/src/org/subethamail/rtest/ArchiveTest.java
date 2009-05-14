/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ArchiveTest.class);

	/** */
	AdminMixin admin;
	PersonMixin pers;
	MailingListMixin ml;
	
	/** */
	public ArchiveTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		
		// Create a list with the person subscribed
		this.pers = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, this.pers.getAddress());
	}
	
	/** */
	public void testDeleteMessage() throws Exception
	{
		// Create two messages, one with subject one with body
		byte[] rawMsg = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), TEST_SUBJECT, TEST_BODY);
		
		this.admin.getPlumber().log("### Calling Injector.inject()");
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
		this.admin.getPlumber().log("### Calling Archiver.getThreads()");
		Long mailId = this.admin.getArchiver().getThreads(this.ml.getId(), 0, 100).get(0).getId();
		
		this.admin.getPlumber().log("### Calling Archiver.deleteMail()");
		this.admin.getArchiver().deleteMail(mailId);
		
		try
		{
			this.admin.getPlumber().log("### Calling Archiver.getMail()");
			this.admin.getArchiver().getMail(mailId);
			fail("able to get deleted mail");
		}
		catch (NotFoundException ex) {}
		
		// TODO:  test when mail has replies or is a reply to other mail
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(ArchiveTest.class);
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(ArchiveTest.class);

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
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(ArchiveTest.class);
	}
}

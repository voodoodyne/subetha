/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.subethamail.common.Utils;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class ThreadingTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin person1;
	
	/** */
	public ThreadingTest(String name) { super(name); }
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.person1 = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, null);
		
		this.person1.getAccountMgr().subscribeMe(this.ml.getId(), this.person1.getEmail());
	}
	
	/** */
	public void testSubjectThreading() throws Exception
	{
		String subj1 = "foo";
		String body1 = Utils.uniqueString();
		byte[] rawMsg1 = this.createMessage(this.person1.getAddress(), this.ml.getAddress(), subj1, body1);
		this.admin.getInjector().inject(this.person1.getAddress().getAddress(), this.ml.getEmail(), rawMsg1);
		
		String subj2 = "Re: foo";
		String body2 = Utils.uniqueString();
		byte[] rawMsg2 = this.createMessage(this.person1.getAddress(), this.ml.getAddress(), subj2, body2);
		this.admin.getInjector().inject(this.person1.getAddress().getAddress(), this.ml.getEmail(), rawMsg2);
		
		List<MailSummary> threads = this.admin.getArchiver().getThreads(this.ml.getId(), 0, 100);
		assertEquals(1, threads.size());
		
		MailSummary root = threads.get(0);
		assertEquals(subj1, root.getSubject());
		assertEquals(1, root.getReplies().size());
		
		MailSummary reply = root.getReplies().get(0);
		assertEquals(subj2, reply.getSubject());
		
		// Let mail get delivered before shutting down
		Thread.sleep(500);
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(ThreadingTest.class);
	}
}

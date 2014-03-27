/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import java.net.URL;

import javax.mail.internet.InternetAddress;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.subethamail.common.MailUtils;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.Utils;
import org.subethamail.core.acct.i.MyListRelationship;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.post.i.MailType;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.BeanMixin;
import org.subethamail.rtest.util.MailingListInfoMixin;
import org.subethamail.rtest.util.MailingListMixin;
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
	AdminMixin admin;
	PersonMixin pers;
	BeanMixin nobody;
	
	/** */
	public MailingListTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.nobody = new BeanMixin();
	}
	
	/** */
	public void testAddRemoveFilter() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress());
		
		this.admin.getListMgr().setFilterDefault(ml.getId(), "org.subethamail.plugin.filter.HoldEverythingFilter");
		
		this.admin.getListMgr().disableFilter(ml.getId(), "org.subethamail.plugin.filter.HoldEverythingFilter");
	}

	/** */
	public void testLookupAlternatives() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress());
		String lastPart = Utils.uniqueString();
		
		URL normalUrl = new URL("http://www.example.com/se/list/" + lastPart);
		this.admin.getAdmin().setListAddresses(ml.getId(), ml.getAddress(), normalUrl);

		// These shouldn't throw NotFoundExeption
		this.nobody.getListMgr().lookup(new URL("http://www.example.com/se/list/" + lastPart + "/"));
		this.nobody.getListMgr().lookup(new URL("http://example.com/se/list/" + lastPart));
	}
	
	/** */
	public void testCreateMailingListForExistingPerson() throws Exception
	{
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress());

		// Should contain a "Your new list" email
		assertEquals(1, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.NEW_MAILING_LIST));
		
		MyListRelationship data = this.nobody.getAccountMgr().getMyListRelationship(ml.getId());
		assertEquals(ml.getEmail(), data.getListEmail());
	}
	
	/** */
	public void testCreateMailingListForNewPerson() throws Exception
	{
		PersonInfoMixin info = new PersonInfoMixin();
		MailingListMixin ml = new MailingListMixin(this.admin, info.getAddress());

		// Should contain a "Your new list" email
		assertEquals(1, this.smtp.size());
		assertEquals(1, this.smtp.count(MailType.NEW_MAILING_LIST));
		
		MyListRelationship data = this.nobody.getAccountMgr().getMyListRelationship(ml.getId());
		assertEquals(ml.getEmail(), data.getListEmail());
	}
	
	/** */
	public void testChangeListAddresses() throws Exception
	{
		this.admin.getAdmin().log("Starting testChangeListAddresses()");
		
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress());
		MailingListInfoMixin next = new MailingListInfoMixin();

		// First just the url
		this.admin.getAdmin().setListAddresses(ml.getId(), ml.getAddress(), next.getUrl());
		
		ListData data = this.admin.getListMgr().getList(ml.getId());
		assertEquals(ml.getEmail(), data.getEmail());
		assertEquals(next.getUrl().toString(), data.getUrl());
		
		// Then just the address
		this.admin.getAdmin().setListAddresses(ml.getId(), next.getAddress(), next.getUrl());
		
		data = this.admin.getListMgr().getList(ml.getId());
		assertEquals(next.getEmail(), data.getEmail());
		assertEquals(next.getUrl().toString(), data.getUrl());
		
		// Then both (back to original
		this.admin.getAdmin().setListAddresses(ml.getId(), ml.getAddress(), ml.getUrl());
		
		data = this.admin.getListMgr().getList(ml.getId());
		assertEquals(ml.getEmail(), data.getEmail());
		assertEquals(ml.getUrl().toString(), data.getUrl());
	}
	
	/** */
	public void testDeleteMailingList() throws Exception
	{
		// Create a mailing list
		MailingListMixin ml = new MailingListMixin(this.admin, this.pers.getAddress());
		
		// Add a message to it
		String subject = Utils.uniqueString();
		
		byte[] rawMsg = this.createMessage(this.pers.getAddress(), ml.getAddress(), subject, TEST_BODY);
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), ml.getEmail(), rawMsg);

		// Find the id of that message
		MailSummary summary = this.admin.getArchiver().getThreads(ml.getId(), 0, 10).get(0);
		
		// Must have valid password
		boolean shouldBeFalse = this.admin.getAdmin().deleteList(ml.getId(), "wrong password");
		assertFalse(shouldBeFalse);
		this.admin.getListMgr().getList(ml.getId());	// should work still
		
		// Now delete the list
		boolean shouldBeTrue = this.admin.getAdmin().deleteList(ml.getId(), this.admin.getPassword());
		assert(shouldBeTrue);
		
		// The list should be gone
		try
		{
			this.admin.getListMgr().getList(ml.getId());
			fail("List was not deleted");
		}
		catch (NotFoundException ex) {}
		
		// The message should be gone
		try
		{
			this.admin.getArchiver().getMail(summary.getId());
			fail("Archived message was not deleted from deleted list");
		}
		catch (NotFoundException ex) {}

		// The person should no longer be subscribed
		assert(this.pers.getAccountMgr().getSelf().getSubscriptions().isEmpty());
		
		// You shouldn't be able to find the search term
		try
		{
			this.admin.getArchiver().search(ml.getId(), subject, 0, 10);
			fail("Able to search deleted list");
		}
		catch (NotFoundException ex) {}
	}
	
	public void testMassSubscribeToMailingList() throws Exception
	{
		String massA = "Foo Bar <foo@bar.com>, \"Jeff\" <jeff@bar.com>";
		String massB = "Foo Bar <foo@bar.com>\n \"Jeff\" <jeff@bar.com>";
		String massC = "Foo Bar <foo@bar.com>\n \"Jeff\" <jeff@bar.com>, \"Jeff2\" <jeff2@bar.com>\n\n\"Jeff3\" <jeff3@bar.com>";
	
		InternetAddress[] addrA = MailUtils.parseMassSubscribe(massA);
		assertEquals("foo@bar.com", addrA[0].getAddress());
		assertEquals("Foo Bar", addrA[0].getPersonal());
		assertEquals("jeff@bar.com", addrA[1].getAddress());
		assertEquals("Jeff", addrA[1].getPersonal());

		InternetAddress[] addrB = MailUtils.parseMassSubscribe(massB);
		assertEquals("foo@bar.com", addrB[0].getAddress());
		assertEquals("Foo Bar", addrB[0].getPersonal());
		assertEquals("jeff@bar.com", addrB[1].getAddress());
		assertEquals("Jeff", addrB[1].getPersonal());

		InternetAddress[] addrC = MailUtils.parseMassSubscribe(massC);
		assertEquals("foo@bar.com", addrC[0].getAddress());
		assertEquals("Foo Bar", addrC[0].getPersonal());
		assertEquals("jeff@bar.com", addrC[1].getAddress());
		assertEquals("Jeff", addrC[1].getPersonal());	
		assertEquals("jeff2@bar.com", addrC[2].getAddress());
		assertEquals("Jeff2", addrC[2].getPersonal());
		assertEquals("jeff3@bar.com", addrC[3].getAddress());
		assertEquals("Jeff3", addrC[3].getPersonal());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(MailingListTest.class);
	}
}

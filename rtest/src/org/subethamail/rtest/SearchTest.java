/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.subethamail.core.lists.i.SearchResult;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonInfoMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class SearchTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	String uniqueString;
	
	PersonMixin pers;
	MailingListMixin ml;
	
	/** */
	public SearchTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		
		PersonInfoMixin info = new PersonInfoMixin();
		this.uniqueString = info.getEmail();	// should be unique enough
		
		// Create a list with the person subscribed
		this.pers = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, this.pers.getAddress());
	}
	
	/** */
	public void testNothingFound() throws Exception
	{
		// The email should be suitable unique
		SearchResult result = this.admin.getArchiver().search(this.ml.getId(), this.uniqueString, 0, 5);
		assertEquals(0, result.getTotal());
		assertEquals(0, result.getHits().size());
	}
	
	/** */
	public void testSimpleFind() throws Exception
	{
		// Create two messages, one with subject one with body
		byte[] rawMsg1 = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), this.uniqueString, TEST_BODY);
		byte[] rawMsg2 = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), TEST_SUBJECT, this.uniqueString);
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg1);
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg2);
		
		SearchResult result = this.admin.getArchiver().search(this.ml.getId(), this.uniqueString, 0, 5);
		assertEquals(2, result.getTotal());
		assertEquals(2, result.getHits().size());
	}
	
	/** */
	public void testListIsolation() throws Exception
	{
		MailingListMixin otherList = new MailingListMixin(this.admin, this.pers.getAddress());
		
		// Create two messages, one with subject one with body
		byte[] rawMsg1 = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), this.uniqueString, TEST_BODY);
		byte[] rawMsg2 = this.createMessage(this.pers.getAddress(), this.ml.getAddress(), TEST_SUBJECT, this.uniqueString);
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg1);
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg2);
		
		// Now search the other list
		SearchResult result = this.admin.getArchiver().search(otherList.getId(), this.uniqueString, 0, 5);
		assertEquals(0, result.getTotal());
		assertEquals(0, result.getHits().size());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SearchTest.class);
	}
}

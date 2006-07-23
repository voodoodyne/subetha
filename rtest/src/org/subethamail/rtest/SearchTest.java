/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.search.i.SimpleResult;
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
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SearchTest.class);

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
		SimpleResult result = this.admin.getIndexer().search(this.uniqueString, 0, 5);
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
		
		this.admin.getIndexer().update();
		
		SimpleResult result = this.admin.getIndexer().search(this.uniqueString, 0, 5);
		assert(result.getTotal() > 0);
		assertEquals(2, result.getHits().size());
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SearchTest.class);
	}
}

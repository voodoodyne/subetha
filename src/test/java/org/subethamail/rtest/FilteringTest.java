/*
 * $Id: InjectorTest.java 1207 2009-05-28 00:50:18Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/trunk/rtest/src/org/subethamail/rtest/InjectorTest.java $
 */

package org.subethamail.rtest;

import static org.junit.Assert.assertEquals;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.junit.Assume;
import org.junit.Before;
import org.subethamail.rtest.util.AdminMixin;
import org.subethamail.rtest.util.MailingListMixin;
import org.subethamail.rtest.util.PersonMixin;
import org.subethamail.rtest.util.SubEthaTestCase;

/**
 * @author Jeff Schnitzer
 */
public class FilteringTest extends SubEthaTestCase
{
	/** */
	AdminMixin admin;
	MailingListMixin ml;
	PersonMixin pers;
	
	/**
	 * Creates a mailing list with two people subscribed.
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.admin = new AdminMixin();
		this.pers = new PersonMixin(this.admin);
		this.ml = new MailingListMixin(this.admin, null, "org.subethamail.plugin.blueprint.FreeForAllBlueprint");
		
		this.pers.getAccountMgr().subscribeMe(this.ml.getId(), this.pers.getEmail());
	}
	
	/** */
	protected void expectPlain(String filename) throws Exception
	{
		byte[] rawMsg = this.createMessageFromFile(filename);
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		
		MimeMessage msg = this.smtp.getLastMessage().getMimeMessage();
		assertEquals(TEST_SUBJECT, msg.getSubject());
		assert msg.getContentType().startsWith("text/plain");
		assert msg.getContent().toString().startsWith("Lorem Ipsum");
	}

	/** */
	protected void expectHtmlUnmolested() throws Exception
	{
		byte[] rawMsg = this.createMessageFromFile("html-only.msg");
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		
		MimeMessage msg = this.smtp.getLastMessage().getMimeMessage();
		assertEquals(TEST_SUBJECT, msg.getSubject());
		assert msg.getContentType().startsWith("text/html");
		assert msg.getContent().toString().startsWith("<!DOCTYPE html");
	}
	
	/** */
	protected void expectPlainAndHtmlUnmolested() throws Exception
	{
		byte[] rawMsg = this.createMessageFromFile("plain-and-html.msg");
		
		this.admin.getInjector().inject(this.pers.getAddress().getAddress(), this.ml.getEmail(), rawMsg);
		Thread.sleep(1000);
		
		MimeMessage msg = this.smtp.getLastMessage().getMimeMessage();
		assertEquals(TEST_SUBJECT, msg.getSubject());
		assert msg.getContentType().startsWith("multipart/alternative");
		
		Multipart parts = (Multipart)msg.getContent();
		assertEquals(2, parts.getCount());
		
		BodyPart plainPart = parts.getBodyPart(0);
		assert plainPart.getContentType().startsWith("text/plain");
		assert plainPart.getContent().toString().startsWith("Lorem Ipsum");
		
		BodyPart htmlPart = parts.getBodyPart(1);
		assert htmlPart.getContentType().startsWith("text/html");
		assert htmlPart.getContent().toString().startsWith("<!DOCTYPE html");
	}
	
	/** */
    @org.junit.Test
	public void testUnfilteredPlain() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.expectPlain("plain-only.msg");
	}

	/** */
    @org.junit.Test
	public void testUnfilteredHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.expectHtmlUnmolested();
	}

	/** */
    @org.junit.Test
	public void testUnfilteredPlainAndHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.expectPlainAndHtmlUnmolested();
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testStripAttachmentPlain() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.StripAttachmentsFilter");

		this.expectPlain("plain-only.msg");
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testStripAttachmentHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.StripAttachmentsFilter");

		this.expectHtmlUnmolested();
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testStripAttachmentPlainAndHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.StripAttachmentsFilter");

		this.expectPlainAndHtmlUnmolested();
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testLeaveAttachmentsPlain() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.LeaveAttachmentsOnServerFilter");

		this.expectPlain("plain-only.msg");
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testLeaveAttachmentsHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.LeaveAttachmentsOnServerFilter");

		this.expectHtmlUnmolested();
	}

	/** Should be no different than without the filter */
    @org.junit.Test
	public void testLeaveAttachmentsPlainAndHtml() throws Exception
	{
        Assume.assumeTrue(ResinTestSetup.exists());
		this.admin.getListMgr().setFilterDefault(this.ml.getId(), "org.subethamail.plugin.filter.LeaveAttachmentsOnServerFilter");

		this.expectPlainAndHtmlUnmolested();
	}
}

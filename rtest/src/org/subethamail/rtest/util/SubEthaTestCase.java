/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * All SubEtha tests require a running smtp server.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaTestCase extends TestCase
{
	/** */
	private static Log log = LogFactory.getLog(SubEthaTestCase.class);

	/** */
	protected Smtp smtp;
	
	/** */
	public SubEthaTestCase(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this.smtp = Smtp.start();
	}
	
	/** */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		
		this.smtp.stop();
		this.smtp = null;
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(SubEthaTestCase.class);
	}
}

/*
 * $Id: AccountTest.java 1075 2009-05-07 06:41:19Z lhoriman $
 * $URL: https://subetha.googlecode.com/svn/branches/resin/rtest/src/org/subethamail/rtest/AccountTest.java $
 */

package org.subethamail.rtest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.rtest.util.AdminMixin;

/**
 * Very basic tests that should be run first
 * 
 * @author Jeff Schnitzer
 */
public class AAATest extends TestCase
{
	/** */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AAATest.class);

	/** */
	public AAATest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	/** */
	public void testFirstThing() throws Exception
	{
		AdminMixin admin = new AdminMixin();
		admin.getPlumber().log("############# FIRST TEST RUN SUCCESSFULLY");
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(AAATest.class);
	}
}

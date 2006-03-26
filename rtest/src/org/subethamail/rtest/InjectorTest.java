/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.injector.i.InjectorRemote;
import org.subethamail.rtest.util.Samples;

/**
 * @author Jeff Schnitzer
 */
public class InjectorTest extends TestCase
{
	/** */
	private static Log log = LogFactory.getLog(InjectorTest.class);

	/** */
	Injector injector;
	
	/** */
	public InjectorTest(String name) { super(name); }
	
	/** */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		Context ctx = new InitialContext();
		
		this.injector = (Injector)ctx.lookup(InjectorRemote.JNDI_NAME);
	}
	
	/** */
	public void testTrivialInjection() throws Exception
	{
		byte[] msg = Samples.getMessage("plain.msg");
		
		this.injector.inject("blah@infohazard.org", msg);
	}
	
	/** */
	public static Test suite()
	{
		return new TestSuite(InjectorTest.class);
	}
}

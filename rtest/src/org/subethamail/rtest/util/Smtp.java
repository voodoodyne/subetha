/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dumbster.smtp.SimpleSmtpServer;

/**
 * Some random useful tools for dealing with Dumbster.
 * 
 * @author Jeff Schnitzer
 */
public class Smtp
{
	/** */
	private static Log log = LogFactory.getLog(Smtp.class);
	
	/** The port we use for dumbster */
	public static final int PORT = 2525;

	/** */
	public static SimpleSmtpServer start()
	{
		return SimpleSmtpServer.start(PORT);
	}
}

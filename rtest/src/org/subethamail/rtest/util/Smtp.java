/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest.util;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.post.i.Constant;
import org.subethamail.core.post.i.MailType;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

/**
 * A useful wrapper for Dumbster that provides some Subetha-specific
 * methods.
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
	SimpleSmtpServer server;
	
	/** */
	public Smtp(SimpleSmtpServer server)
	{
		this.server = server;
	}
	
	/** */
	public static Smtp start()
	{
		return new Smtp(SimpleSmtpServer.start(PORT));
	}
	
	/** */
	public void stop()
	{
		this.server.stop();
	}

	/** */
	public int size()
	{
		return this.server.getReceivedEmailSize();
	}
	
	/** */
	@SuppressWarnings("unchecked")
	public Iterator<SmtpMessage> iterator()
	{
		return this.server.getReceivedEmail();
	}
	
	/** */
	public SmtpMessage get(int index)
	{
		Iterator<SmtpMessage> it = this.iterator();
		
		SmtpMessage result = it.next();
		
		for (int i=0; i<index; i++)
			result = it.next();
		
		return result;
	}
	
	/**
	 * @return the number of messages of the specified type 
	 */
	public int count(MailType type)
	{
		int count = 0;
		
		Iterator<SmtpMessage> it = this.iterator();
		while (it.hasNext())
		{
			SmtpMessage msg = it.next();
			
			if (msg.getHeaderValue("Subject").startsWith(type.name()))
				count++;
		}
		
		return count;
	}
	
	/**
	 * @return the number of messages containing the specified subject 
	 */
	public int countSubject(String subject)
	{
		int count = 0;
		
		Iterator<SmtpMessage> it = this.iterator();
		while (it.hasNext())
		{
			SmtpMessage msg = it.next();
			
			if (msg.getHeaderValue("Subject").contains(subject))
				count++;
		}
		
		return count;
	}
	
	/**
	 * Gets an embedded token from a message.
	 * 
	 * @throws IllegalArgumentException if msg has no token
	 */
	public static String extractToken(SmtpMessage msg)
	{
		String body = msg.getBody();
		
		int start = body.indexOf(Constant.DEBUG_TOKEN_BEGIN);
		if (start < 0)
			throw new IllegalStateException("Missing token from email: " + body);
		
		start += Constant.DEBUG_TOKEN_BEGIN.length();
		
		int end = body.indexOf(Constant.DEBUG_TOKEN_END, start);
		if (end < 0)
			throw new IllegalStateException("Missing token from email: " + body);
		
		return body.substring(start, end);
	}
}
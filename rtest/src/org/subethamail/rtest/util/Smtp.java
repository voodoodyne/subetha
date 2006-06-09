/*
 * $Id$
 * $URL$
 */

package org.subethamail.rtest.util;

import java.io.IOException;
import java.util.Iterator;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.post.i.Constant;
import org.subethamail.core.post.i.MailType;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * A useful wrapper for Wiser that provides some Subetha-specific
 * methods.
 * 
 * @author Jeff Schnitzer
 */
public class Smtp extends Wiser
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(Smtp.class);
	
	/** The port we use for dumbster */
	public static final int PORT = 2525;

	public Smtp()
	{
		super();
		this.setPort(PORT);
	}

	/** */
	public int size()
	{
		return super.getMessages().size();
	}
	
	/** */
	public WiserMessage get(int index)
	{
		return super.getMessages().get(index);
	}
	
	/** Gets the Nth instance of the specified mail type 
	 * @throws MessagingException 
	 */
	public WiserMessage get(MailType type, int index) throws MessagingException
	{
		int count = 0;
		
		Iterator<WiserMessage> it = super.getMessages().iterator();
		while (it.hasNext())
		{
			WiserMessage msg = it.next();

			if (msg.getMimeMessage().getSubject().startsWith(type.name()))
			{
				if (count == index)
					return msg;
				else
					count++;
			}
		}

		return null;
	}
	
	/**
	 * @return the number of messages of the specified type 
	 * @throws MessagingException 
	 */
	public int count(MailType type) throws MessagingException
	{
		int count = 0;
		
		Iterator<WiserMessage> it = super.getMessages().iterator();
		while (it.hasNext())
		{
			WiserMessage msg = it.next();
			
			if (msg.getMimeMessage().getSubject().startsWith(type.name()))
				count++;
		}
		
		return count;
	}
	
	/**
	 * @return the number of messages containing the specified subject 
	 * @throws MessagingException 
	 */
	public int countSubject(String subject) throws MessagingException
	{
		int count = 0;
		
		Iterator<WiserMessage> it = super.getMessages().iterator();
		while (it.hasNext())
		{
			WiserMessage msg = it.next();
			
			if (msg.getMimeMessage().getSubject().contains(subject))
				count++;
		}
		
		return count;
	}
	
	/**
	 * Gets an embedded token from a message.
	 * @throws MessagingException 
	 * @throws IOException 
	 * 
	 * @throws IllegalArgumentException if msg has no token
	 */
	public static String extractToken(WiserMessage msg) throws IOException, MessagingException
	{
		String body = (String) msg.getMimeMessage().getContent();
		
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
/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.common;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Offers static utility methods to help processing javamail
 * objects. 
 * 
 * @author Jeff Schnitzer
 */
public class MailUtils
{
	/** */
	private static Log log = LogFactory.getLog(MailUtils.class);
	
	/** */
	public static final String HDR_MESSAGE_ID = "Message-ID";	
	public static final String HDR_FROM = "From";	
	
	/**
	 * @return the Message-ID header from a msg, or null if no message-id header present
	 */
	public static String getMessageId(Message msg) throws MessagingException
	{
		String[] ids = msg.getHeader(HDR_MESSAGE_ID);
		
		if (ids == null || ids.length == 0)
			return null;
		
		// This is possible but shouldn't happen
		if (ids.length > 1)
		{
			if (log.isWarnEnabled())
			{
				log.warn("Got message with multiple message ids:");
				for (String id: ids)
					log.warn(id);
			}
		}
		
		// Just return the first one
		return ids[0]; 
	}
	
	/**
	 * There may be 0 or more From headers in a mail message.  The return value
	 * will either be null or a combination of them like this:
	 * 
	 * bob@dobbs.com <Bob Dobbs>, foo@bar.com <Foo Bar>, blah@blah.com
	 * 
	 * @return a user-presentable version of the From field(s) of the message,
	 *  or null if there were no From headers.
	 */
	public static String getFrom(Message msg) throws MessagingException
	{
		Address[] froms = msg.getFrom();
		
		if (froms == null || froms.length == 0)
			return null;
		
		if (froms.length == 1)
		{
			return froms[0].toString();
		}
		else
		{
			StringBuffer buf = new StringBuffer();
			
			for (int i=0; i<froms.length; i++)
			{
				if (i != 0)
					buf.append(", ");
				
				buf.append(froms[i].toString());
			}
			
			return buf.toString();
		}
	}
}

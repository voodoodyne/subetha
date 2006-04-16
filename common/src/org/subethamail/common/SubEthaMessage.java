/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.smtp.SMTPMessage;


/**
 * Our version of the MimeMessage.  Note this must extend Sun's
 * SMTPMessage so that we can set the envelope From (for VERP).
 * SMTPMessage works in collusion with the behind-the-scenes
 * SMTPTransport to allow this override.
 * 
 * Note this means it will NOT work with any other JavaMail provider.
 * 
 * @author Jeff Schnitzer
 */
public class SubEthaMessage extends SMTPMessage
{
	/** */
	private static Log log = LogFactory.getLog(SubEthaMessage.class);
	
	/** */
	public static final String HDR_MESSAGE_ID = "Message-ID";
	public static final String HDR_IN_REPLY_TO = "In-Reply-To";
	public static final String HDR_REFERENCES = "References";

	/** */
	public SubEthaMessage(Session session, InputStream is) throws MessagingException
	{
		super(session, is);
	}
	
	/** */
	public SubEthaMessage(Session session, byte[] mail) throws MessagingException
	{
		this(session, new ByteArrayInputStream(mail));
	}
	
	/**
	 * Checks for any attempt to rewrite the Message-ID and ignores it.
	 * This behavior of JavaMail is just dumb.
	 */
	@Override
	public void setHeader(String name, String value) throws MessagingException
	{
		if (name.equals(HDR_MESSAGE_ID))
		{
			if (this.getMessageID() != null)
				return;
		}
		
		super.setHeader(name, value);
	}
	
	/**
	 * @return the value of the In-Reply-To header field, or null if none 
	 */
	public String getInReplyTo() throws MessagingException
	{
		// Note that we have a lot of work to do because there could
		// be a lot of garbage in this field.  We want to locate the
		// first instance of <blahblahblah>, if it exists.
		//
		// See http://cr.yp.to/immhf/thread.html

		String[] values = this.getHeader(HDR_IN_REPLY_TO);
		
		if (values == null || values.length == 0)
			return null;
		
		if (values.length > 1)
			log.error("Found a message with " + values.length + " In-Reply-To fields");

		for (String field: values)
		{
			int start = field.indexOf('<');
			if (start < 0)
				continue;
			
			int end = field.indexOf('>', start);
			if (end < 0)
				continue;
			
			return field.substring(start, end+1);
		}
		
		return null;
	}
	
	/**
	 * @return all the references, in the same order as the header field. 
	 */
	public String[] getReferences() throws MessagingException
	{
		String[] values = this.getHeader(HDR_REFERENCES);
		
		if (values == null || values.length == 0)
			return null;
		
		if (values.length > 1)
			log.error("Found a message with " + values.length + " References fields");

		StringTokenizer tokenizer = new StringTokenizer(values[0]);
		
		String[] toks = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens())
		{
			toks[i] = tokenizer.nextToken();
			i++;
		}
		
		return toks;
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
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
	public SubEthaMessage(Session session) throws MessagingException
	{
		super(session);
	}
	
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
		
		int count = tokenizer.countTokens();
		if (count == 0)
			return null;
		
		String[] toks = new String[count];
		int i = 0;
		while (tokenizer.hasMoreTokens())
		{
			toks[i] = tokenizer.nextToken();
			i++;
		}
		
		return toks;
	}

	/**
	 * Generates and assigns a new message id.  Uses the same algorithm
	 * as JavaMail.
	 */
	public void replaceMessageID() throws MessagingException
	{
		String suffix = null;

		InternetAddress addr = InternetAddress.getLocalAddress(this.session);
		if (addr != null)
			suffix = addr.getAddress();
		else
			suffix = "subetha@localhost"; // worst-case default

		StringBuffer s = new StringBuffer();

		// Unique string is <hashcode>.<currentTime>.SubEtha.<suffix>
		s.append(s.hashCode())
			.append('.')
			.append(System.currentTimeMillis())
			.append('.')
			.append("SubEtha.")
			.append(suffix);

		super.setHeader(SubEthaMessage.HDR_MESSAGE_ID, "<" + s + ">");
	}
	
	/**
	 * @return a flattened container of all the textual parts in this
	 *  message.
	 */
	public List<String> getTextParts() throws MessagingException, IOException
	{
		List<String> textParts = new ArrayList<String>();
		
		getTextParts(this, textParts);
		
		return textParts;
	}
	
	/**
	 * Recursive method for getting all text parts
	 */
	protected static void getTextParts(Part part, List<String> textParts) throws MessagingException, IOException
	{
		Object content = part.getContent();
		
		if (content instanceof String)
		{
			textParts.add((String)content);
		}
		else if (content instanceof Multipart)
		{
			Multipart multipartContent = (Multipart)content;
			
			for (int i=0; i<multipartContent.getCount(); i++)
			{
				// Recurse
				getTextParts(multipartContent.getBodyPart(i), textParts);
			}
		}
		else
		{
			log.debug("Didn't know what to do with content " + content);
		}
	}
	
	/**
	 * Stupidly, saveChanges() resaves even if nothing has changed.
	 */
	public void saveIfNecessary() throws MessagingException
	{
		if (!this.saved)
			this.saveChanges();
	}
}

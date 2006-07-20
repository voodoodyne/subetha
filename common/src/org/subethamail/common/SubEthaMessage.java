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
	public static final String HDR_X_LOOP = "X-Loop";
	public static final String HDR_CONTENT_TYPE = "Content-Type";
	public static final String HDR_CONTENT_DISPOSITION = "Content-Disposition";

	/**
	 * Header for parts that have been detached; holds the original
	 * content type.
	 */
	public static final String HDR_ORIGINAL_CONTENT_TYPE = "X-Original-Content-Type";
	
	/** 
	 * The mime type for detached attachments.  The content will be
	 * the attachment id as an ascii string. 
	 */
	public static final String DETACHMENT_MIME_TYPE = "application/subetha-detachment";
	
	/** */
	public SubEthaMessage(Session session) throws MessagingException
	{
		super(session);
	}
	
	/** */
	public SubEthaMessage(Session session, InputStream is) throws MessagingException
	{
		super(session, is);
		// Always always assume we have been modified, otherwise changes
		// get ignored.  The modified flag does not get reliably set by
		// the various methods that should set the fucking flag.
		this.modified = true;
		this.saved = false;
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
	public List<Part> getParts() throws MessagingException, IOException
	{
		List<Part> parts = new ArrayList<Part>();
			
		getParts(this, parts);
			
		return parts;
	}
	
	/** */
	protected static void getParts(Part part, List<Part> parts) throws MessagingException, IOException
	{
		Object content = part.getContent();

		if (content instanceof Part)
		{
			Part contentPart = (Part)content;
			getParts(contentPart, parts);
		}
		else if (content instanceof Multipart)
		{
			Multipart multipartContent = (Multipart)content;
			
			for (int i=0; i<multipartContent.getCount(); i++)
			{
				// Recurse
				getParts(multipartContent.getBodyPart(i), parts);
			}
		}
		else
		{
			// This was a content-containing part, no recursion.
			parts.add(part);
		}
	}	
		
	/**
	 * Call this if you make any changes to the message, or its parts.
	 */
	public void save() throws MessagingException
	{
		try
		{
			Object contents = this.getContent();
			if (contents instanceof Multipart)
			{
				//this is dumb, but it is a javamail bug.
				Multipart mp = (Multipart) contents;
				this.setContent(mp);
			}
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
		
		if (!this.saved)
			this.saveChanges();
		
	}

	/**
	 * Tests whether or not there is an existing x-loop header for the list email address.
	 */
	public boolean hasXLoop(String email) throws MessagingException
	{
		String[] xloops = this.getHeader(HDR_X_LOOP);
		if (xloops != null)
		{
			for (String xloop: xloops)
			{
				if (email.equals(xloop))
					return true;
			}
		}

		return false;
	}

	/**
	 * Adds an x-loop header
	 */
	public void addXLoop(String email) throws MessagingException
	{
		this.addHeader(HDR_X_LOOP, email);
	}

	/**
	 * @return the text that should be indexed
	 */
	public String getIndexableText() throws MessagingException, IOException
	{
		StringBuilder buf = new StringBuilder();

		for (Part part: this.getParts())
		{
			if (part.getContentType().toLowerCase().startsWith("text/"))
			{
				buf.append(part.getContent().toString());
			}
		}
		
		return buf.toString();
	}
}
/*
 */

package org.subethamail.entity;

import java.io.IOException;

import javax.mail.MessagingException;

import org.hibernate.search.bridge.StringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.util.Producers;

/**
 * Converts a byte[] of message content into a String for indexing
 * by Hibernate Search (aka Lucene).
 * 
 * @author Jeff Schnitzer
 */
public class MessageContentBridge implements StringBridge
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(MessageContentBridge.class);

	@Override
	public String objectToString(Object arg0)
	{
		byte[] content = (byte[]) arg0;

		try
		{
			SubEthaMessage msg = new SubEthaMessage(Producers.instance().getMailSession(), content);
			return msg.getIndexableText();
		}
		catch (MessagingException ex)
		{
			log.error("Exception indexing content", ex);
		}
		catch (IOException ex)
		{
			log.error("Exception indexing content", ex);
		}
		
		return null;
	}
	
}


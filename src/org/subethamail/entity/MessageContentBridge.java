/*
 */

package org.subethamail.entity;

import java.io.IOException;
import java.util.logging.Level;

import javax.mail.MessagingException;

import lombok.extern.java.Log;

import org.hibernate.search.bridge.StringBridge;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.util.Producers;

/**
 * Converts a byte[] of message content into a String for indexing
 * by Hibernate Search (aka Lucene).
 * 
 * @author Jeff Schnitzer
 */
@Log
public class MessageContentBridge implements StringBridge
{
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
		    log.log(Level.SEVERE,"Exception indexing content", ex);
		}
		catch (IOException ex)
		{
		    log.log(Level.SEVERE,"Exception indexing content", ex);
		}
		
		return null;
	}
	
}


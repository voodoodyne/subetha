/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

/**
 * Listener which counts the number of messages that it receives. 
 * 
 * @author Jeff Schnitzer
 */
public class CountingListener implements SimpleMessageListener
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CountingListener.class);
	
	/** Number of messages received total */
	int totalCount;
	
	/** */
	public CountingListener()
	{
	}

	/** Always accept everything */
	public boolean accept(String from, String recipient)
	{
		return true;
	}

	/** Indicate we have one more */
	public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException
	{
		// A little extra buffering can't hurt
		data = new BufferedInputStream(data);
		
		// Read the whole stream
		while (data.read() >= 0)
			;	// do nothing
		
		this.totalCount++;
	}
	
	/** */
	public int getTotalCount()
	{
		return this.totalCount;
	}
}

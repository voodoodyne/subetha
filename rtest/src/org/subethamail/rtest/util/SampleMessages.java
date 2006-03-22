/*
 * $Id: FavoriteBlogTest.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/rtest/src/com/blorn/rtest/acct/FavoriteBlogTest.java $
 */

package org.subethamail.rtest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jeff Schnitzer
 */
public class SampleMessages
{
	/** */
	private static Log log = LogFactory.getLog(SampleMessages.class);

	/**
	 * Gets a simple message with no attachments
	 */
	public static byte[] getPlainMessage() throws IOException
	{
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/subethamail/rtest/msg/plain.msg");
		if (inStream == null)
			throw new IllegalStateException("Couldn't find resource");
		
		return streamToBytes(inStream);
	}
	
	/**
	 * Creates a byte array from a stream.
	 */
	public static byte[] streamToBytes(InputStream inStream) throws IOException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while (inStream.available() > 0)
			outStream.write(inStream.read());
		
		return outStream.toByteArray();
	}
}

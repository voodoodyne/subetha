package org.subethamail.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple output stream that writes into a byte array.
 * No resizing of the array will take place; if it overflows,
 * an exception results.
 */
public class SimpleByteArrayOutputStream extends OutputStream
{
	byte[] buffer;
	int pos;
	
	/** */
	public SimpleByteArrayOutputStream(byte[] buf)
	{
		this.buffer = buf;
	}

	/** */
	public byte[] getBuffer()
	{
		return this.buffer;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		if ((this.pos + 1) >= this.buffer.length)
			throw new IOException("Exceeded maximum length of buffer");

		this.buffer[this.pos++] = (byte)b;
	}

}
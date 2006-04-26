/*
 * $Id: Converter.java 169 2006-04-24 08:01:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/common/src/org/subethamail/common/Converter.java $
 */
package org.subethamail.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an InputStream wrapper which takes notice when a
 * threshold (number of bytes) is about to be read.  This can
 * be used to limit input data, swap readers, etc.
 *  
 * @author Jeff Schnitzer
 */
abstract public class ThresholdingInputStream extends InputStream
{
	/** */
	protected InputStream input;
	
	/** Max number of bytes to read */
	int threshold;
	
	/** Number of bytes read so far */
	int countRead = 0;
	
	/**
	 */
	public ThresholdingInputStream(InputStream base, int thresholdBytes)
	{
		this.input = base;
		this.threshold = thresholdBytes;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException
	{
		return this.input.available();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		this.input.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public void mark(int readlimit)
	{
		this.input.mark(readlimit);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported()
	{
		return this.input.markSupported();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException
	{
		this.checkThreshold(1);
		
		int result = this.input.read();
		
		this.countRead++;
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		this.checkThreshold(len);
		
		int result = this.input.read(b, off, len);
		
		if (result > 0)
			this.countRead += result;
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException
	{
		this.checkThreshold(b.length);
		
		int result = this.input.read(b);
		
		if (result > 0)
			this.countRead += result;
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public void reset() throws IOException
	{
		this.input.reset();
		
		this.countRead = 0;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException
	{
		return this.input.skip(n);
	}

	/**
	 * Checks whether reading count bytes would cross the limit.
	 */
	protected void checkThreshold(int count) throws IOException
	{
		int predicted = this.countRead + count; 
		if (predicted > this.threshold)
			this.thresholdReached(this.countRead, predicted);
	}
	
	/**
	 * @return the current threshold value.
	 */
	public int getThreshold()
	{
		return this.threshold;
	}
	
	/**
	 * Called when the threshold is about to be exceeded.  This isn't
	 * exact; it's called whenever a read would occur that would
	 * cross the amount.
	 * 
	 * @param current is the current number of bytes that have been read
	 * @param predicted is the total number after the read completes
	 */
	abstract protected void thresholdReached(int current, int predicted) throws IOException;
}

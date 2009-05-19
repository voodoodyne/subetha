/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.common.io;

import java.io.IOException;

/**
 * Thrown by LimitingInputStream when a limit is exceeded.
 * 
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class LimitExceededException extends IOException
{
	/** */
	int limit;
	int read;
	int predicted;
	
	/** */
	public LimitExceededException(int limit, int read, int predicted)
	{
		super("Predicted size " + predicted + " exceeds limit " + limit);
		
		this.limit = limit;
		this.read = read;
		this.predicted = predicted;
	}

	/** The limit that was crossed */
	public int getLimit()
	{
		return this.limit;
	}

	/** The number of bytes attempted to read */
	public int getPredicted()
	{
		return this.predicted;
	}

	/** The number of bytes actually read */
	public int getRead()
	{
		return this.read;
	}
}

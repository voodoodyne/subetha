/*
 * $Id: Converter.java 169 2006-04-24 08:01:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/common/src/org/subethamail/common/Converter.java $
 */
package org.subethamail.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an InputStream wrapper which limits the amount of data
 * which can be read.  If the threshold is crossed, a LimitException
 * is thrown.
 *  
 * @author Jeff Schnitzer
 */
public class LimitingInputStream extends ThresholdingInputStream
{
	/**
	 * Limit to limitBytes number of bytes read.
	 */
	public LimitingInputStream(InputStream base, int limitBytes)
	{
		super(base, limitBytes);
	}

	@Override
	protected void thresholdReached(int current, int predicted) throws IOException
	{
		throw new LimitExceededException(this.getThreshold(), current, predicted);
	}
}

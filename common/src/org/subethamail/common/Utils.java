/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Some random utility methods.
 *
 * @author Jeff Schnitzer
 */
public final class Utils
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(Utils.class);

	/** default constructor prevents util class from being created. */
	private Utils() {}

	/**
	 * @return unique string
	 */
	public static String uniqueString()
	{
		Object o = new Object();
		String baseName = Long.toString(System.currentTimeMillis(), 36); 
		return baseName + "-" + o.hashCode();
	}	
}

/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.common;


/**
 * Some random utility methods.
 *
 * @author Jeff Schnitzer
 */
public final class Utils
{
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

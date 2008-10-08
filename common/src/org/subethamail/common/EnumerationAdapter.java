/*
 * $Id: Geometry.java,v 1.2 2003/09/30 06:05:13 jeff Exp $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.common;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Converts iterator to Enumeration interface
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("unchecked")
public class EnumerationAdapter implements Enumeration
{
	/** */
	protected Iterator<?> iterator;

	/**
	 */
	public EnumerationAdapter(Iterator<?> orig)
	{
		this.iterator = orig;
	}

	/* (non-Javadoc)
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		return this.iterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement()
	{
		return this.iterator.next();
	}
}


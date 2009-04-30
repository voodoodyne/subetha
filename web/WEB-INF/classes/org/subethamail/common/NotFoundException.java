/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

/** Generically indicates that something wasn't found (ie, an entity).
 **/
@SuppressWarnings("serial")
public class NotFoundException extends Exception
{
	/**
	 */
	public NotFoundException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public NotFoundException(Throwable cause)
	{
		super(cause);
	}
}


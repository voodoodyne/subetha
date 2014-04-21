/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

/**
 * Generically indicates that something wasn't found (ie, an entity).
 */
public class NotFoundException extends Exception
{
	private static final long serialVersionUID = 1L;
	
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


/*
 * $Id: NameAlreadyTakenException.java,v 1.1 2003/09/04 06:24:48 jeff Exp $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

/**
 * Generically indicates that something wasn't found (ie, an entity)
 */
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


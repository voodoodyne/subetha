/*
 * $Id: NameAlreadyTakenException.java,v 1.1 2003/09/04 06:24:48 jeff Exp $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.pluginapi;

/**
 * Exception indicates that a message should be silently dropped.
 */
public class IgnoreException extends Exception
{
	/**
	 */
	public IgnoreException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public IgnoreException(Throwable cause)
	{
		super(cause);
	}
}


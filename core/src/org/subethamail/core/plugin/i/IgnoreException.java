/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.plugin.i;

/**
 * Exception indicates that a message should be silently dropped.
 */
@SuppressWarnings("serial")
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


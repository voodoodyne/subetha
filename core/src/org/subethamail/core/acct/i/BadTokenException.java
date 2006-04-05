/*
 * $Id: BadTokenException.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.acct.i;

/**
 * Thrown when a token could not be properly decrypted.
 */
@SuppressWarnings("serial")
public class BadTokenException extends Exception
{
	/**
	 */
	public BadTokenException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public BadTokenException(Throwable cause)
	{
		super(cause);
	}
}


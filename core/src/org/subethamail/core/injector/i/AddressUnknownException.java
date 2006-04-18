/*
 * $Id: BadTokenException.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.injector.i;

/**
 * Thrown when the mail is injected that doesn't belong to us.
 */
@SuppressWarnings("serial")
public class AddressUnknownException extends Exception
{
	/**
	 */
	public AddressUnknownException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public AddressUnknownException(Throwable cause)
	{
		super(cause);
	}
}


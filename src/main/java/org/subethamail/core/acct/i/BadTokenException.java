/*
 * $Id: BadTokenException.java 263 2006-05-04 20:58:25Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.acct.i;

import javax.ejb.ApplicationException;

/**
 * Thrown when a token could not be properly decrypted.
 */
@ApplicationException(rollback=true)
public class BadTokenException extends Exception
{
	private static final long serialVersionUID = 1L;

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


/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.acct.i;

import javax.ejb.ApplicationException;

/**
 * Thrown when a token could not be properly decrypted.
 */
@SuppressWarnings("serial")
@ApplicationException(rollback=true)
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


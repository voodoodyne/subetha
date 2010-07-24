/*
 * $Id: NotFoundException.java 560 2006-05-29 21:30:15Z skot $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

import javax.ejb.ApplicationException;

/** Indicates that something happened while importing messages.
 **/
@ApplicationException(rollback=true)
public class ImportMessagesException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 */
	public ImportMessagesException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public ImportMessagesException(Throwable cause)
	{
		super(cause);
	}
}
/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

/** 
 * Generically indicates that a search couldn't be executed.
 */
@SuppressWarnings("serial")
public class SearchException extends Exception
{
	/**
	 */
	public SearchException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public SearchException(Throwable cause)
	{
		super(cause);
	}
}


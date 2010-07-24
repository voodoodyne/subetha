/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.common;

/** 
 * Generically indicates that a search couldn't be executed.
 */
public class SearchException extends Exception
{
	private static final long serialVersionUID = 1L;
	
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


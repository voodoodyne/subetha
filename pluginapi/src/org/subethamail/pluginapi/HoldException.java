/*
 * $Id: NameAlreadyTakenException.java,v 1.1 2003/09/04 06:24:48 jeff Exp $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.pluginapi;

/**
 * Exception indicates that a message should be held for moderation.
 * The msg is a user-presentable explanation of why which will be
 * included in the mail back to the user.
 */
public class HoldException extends Exception
{
	/**
	 */
	public HoldException(String msg)
	{
		super(msg);
	}
	
	/**
	 */
	public HoldException(Throwable cause)
	{
		super(cause);
	}
}


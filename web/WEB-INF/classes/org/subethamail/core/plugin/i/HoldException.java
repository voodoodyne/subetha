/*
 * $Id: HoldException.java 263 2006-05-04 20:58:25Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.plugin.i;

/**
 * Exception indicates that a message should be held for moderation.
 * The msg is a user-presentable explanation of why which will be
 * included in the mail back to the user.
 */
@SuppressWarnings("serial")
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


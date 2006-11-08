/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.entity.i;

import javax.ejb.ApplicationException;



/**
 * Thrown when a permission was needed but not available.
 */
@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class PermissionException extends Exception
{
	Permission needed;
	
	/**
	 */
	public PermissionException(Permission needed)
	{
		super("Requires permission '" + needed.getPretty() + "'. " + 
				"This means that you probably need to login first in order to perform an action. " +
				"If you do not remember your password, click the 'forgot' link at the top of the " +
				"page to have your password sent to you.");

		this.needed = needed;
	}
	
	/**
	 */
	public Permission getNeeded()
	{
		return this.needed;
	}
}


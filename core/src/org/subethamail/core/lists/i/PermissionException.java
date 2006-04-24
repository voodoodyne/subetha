/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.lists.i;

import javax.ejb.ApplicationException;

import org.subethamail.common.Permission;

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
		super("Requires permission " + needed);
		
		this.needed = needed;
	}
	
	/**
	 */
	public Permission getNeeded()
	{
		return this.needed;
	}
}


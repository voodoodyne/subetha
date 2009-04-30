/*
 * $Id: InvalidListDataException.java 778 2006-10-01 19:41:43Z lhoriman $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.admin.i;

import javax.ejb.ApplicationException;

/**
 * Thrown when trying to create a mailing list with bad input data
 */
@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class InvalidListDataException extends Exception
{
	/** */
	boolean ownerAddress;
	boolean verpAddress;
	
	/** */
	public InvalidListDataException() {}
	
	/**
	 */
	public InvalidListDataException(String msg, boolean ownerAddress, boolean verpAddress)
	{
		super(msg);
		
		this.ownerAddress = ownerAddress;
		this.verpAddress = verpAddress;
	}
	
	/**
	 */
	public InvalidListDataException(Throwable cause, boolean ownerAddress, boolean verpAddress)
	{
		super(cause);
		
		this.ownerAddress = ownerAddress;
		this.verpAddress = verpAddress;
	}

	/**
	 * @return true if the address conflicts with a -owner address
	 */
	public boolean isOwnerAddress()
	{
		return this.ownerAddress;
	}

	/**
	 * @return true if the address conflicts with a VERP address
	 */
	public boolean isVerpAddress()
	{
		return this.verpAddress;
	}

}


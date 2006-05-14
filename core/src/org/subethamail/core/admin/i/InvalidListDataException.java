/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.admin.i;

/**
 * Thrown when trying to create a mailing list with bad input data
 */
@SuppressWarnings("serial")
public class InvalidListDataException extends Exception
{
	/** */
	boolean ownerAddress;
	boolean verpAddress;
	
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


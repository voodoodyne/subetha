/*
 * $Id: BadTokenException.java 86 2006-02-22 03:36:01Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/ejb/NameAlreadyTakenException.java,v $
 */

package org.subethamail.core.admin.i;

/**
 * Thrown when a mailing list could not be created.
 */
public class CreateMailingListException extends Exception
{
	/** */
	boolean addressTaken;
	boolean urlTaken;
	
	/**
	 */
	public CreateMailingListException(String msg, boolean duplicateAddress, boolean duplicateUrl)
	{
		super(msg);
		
		this.addressTaken = duplicateAddress;
		this.urlTaken = duplicateUrl;
	}
	
	/**
	 */
	public CreateMailingListException(Throwable cause, boolean duplicateAddress, boolean duplicateUrl)
	{
		super(cause);
		
		this.addressTaken = duplicateAddress;
		this.urlTaken = duplicateUrl;
	}

	/**
	 * @return true if the address is already occupied by another list. 
	 */
	public boolean isAddressTaken()
	{
		return this.addressTaken;
	}

	/**
	 * @return true if the url is already occupied by another list.
	 */
	public boolean isUrlTaken()
	{
		return this.urlTaken;
	}

}


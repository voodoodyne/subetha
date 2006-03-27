/*
 * $Id: Self.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/Self.java $
 */

package org.subethamail.core.acct.i;


/**
 * Some detail about the current user, suitable for display on
 * a page for editing data.
 *
 * @author Jeff Schnitzer
 */
public class Self extends PersonData
{
	/** */
	// TODO:  add list of subscriptions

	/**
	 */
	public Self() {}
	
	/**
	 */
	public Self(Long id, 
				String name,
				String[] emailAddresses)
	{
		super(id, name, emailAddresses);
		
	}
	
}

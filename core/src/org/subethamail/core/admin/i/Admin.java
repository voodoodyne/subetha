/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.admin.i;

import java.util.Collection;

import javax.ejb.Local;

/**
 * Administrative interface for managing the site.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface Admin
{
	/** */
	public static final String JNDI_NAME = "subetha/Admin/local";

	/**
	 * Creates a mailing list.  If any of the initial owner addresses
	 * have not been registered, accounts will be created without confirmation.
	 * 
	 * @param address is a valid email address
	 * @param url is a valid list URL, including the /list/ portion.
	 * @param initialOwners is a list of email addresses.
	 * 
	 * @throws CreateMailingListException if the address or url are already in use.
	 */
	public Long createMailingList(String address, String url, Collection<String> initialOwners) throws CreateMailingListException;
}

/*
 * $Id: ListWizard.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/ListWizard.java $
 */

package org.subethamail.core.admin.i;

import java.util.Collection;

import javax.ejb.Local;
import javax.mail.internet.InternetAddress;


/**
 * Administrative interface for managing the site.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface ListWizard
{
	/** */
	public static final String JNDI_NAME = "subetha/ListWizard/local";
	
	/**
	 * @return a list of all known blueprints. 
	 */
	public Collection<BlueprintData> getBlueprints();

	/**
	 * Creates a mailing list and configures it to a blueprint.  
	 * 
	 * @see Admin#createMailingList(InternetAddress, String, String, InternetAddress[])
	 * 
	 * @param blueprint is the id of a blueprint object from getBlueprints().
	 */
	public Long createMailingList(InternetAddress address, String url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException;
}

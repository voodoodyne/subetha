package org.subethamail.core.admin.i;

import java.net.URL;
import java.util.Collection;

import javax.mail.internet.InternetAddress;


/**
 * Administrative interface for managing the site.
 * 
 * @author Jeff Schnitzer
 */
public interface ListWizard
{
	/**
	 * @return a list of all known blueprints. 
	 */
	public Collection<BlueprintData> getBlueprints();

	/**
	 * Creates a mailing list and configures it to a blueprint.  
	 * 
	 * @see Admin#createMailingList(InternetAddress, URL, String, InternetAddress[])
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException;
}

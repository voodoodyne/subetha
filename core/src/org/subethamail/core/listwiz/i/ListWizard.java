/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.listwiz.i;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.ejb.Local;
import javax.mail.internet.InternetAddress;

import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.CreateMailingListException;

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
	public List<BlueprintData> getBlueprints();

	/**
	 * Creates a mailing list and configures it to a blueprint.  
	 * 
	 * @see Admin#createMailingList(InternetAddress, URL, String, Collection)
	 * 
	 * @param blueprint is the id of a blueprint object from getBlueprints().
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, Collection<InternetAddress> initialOwners, String blueprintId) throws CreateMailingListException;
}

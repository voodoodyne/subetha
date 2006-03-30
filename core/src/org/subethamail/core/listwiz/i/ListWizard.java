/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.listwiz.i;

import java.util.List;

import javax.ejb.Local;

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
	 * Creates a mailing list defined by an archetype.
	 * 
	 * @param blueprint is the classname of a blueprint object.
	 */
	public Long createMailingList(String address, String url, String blueprintId);
}

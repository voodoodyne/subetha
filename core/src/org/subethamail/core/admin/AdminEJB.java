/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.admin;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.AdminRemote;
import org.subethamail.core.admin.i.CreateMailingListException;

/**
 * Implementation of the Admin interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="Admin")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class AdminEJB implements Admin, AdminRemote
{
	/** */
	private static Log log = LogFactory.getLog(AdminEJB.class);

	/**
	 * @see Admin#createMailingList(String, String)
	 */
	public Long createMailingList(String address, String url, Collection<String> initialOwners) throws CreateMailingListException
	{
		//TODO
		
		// First make sure we have a safe address and url.  Check
		// for validity and duplicates.
		
		// Then ensure that all inital owners have accounts, and 
		// build a list of the associated Person objects
		
		// Then create the mailing list and attach the owners.
		
		return null;
	}

}

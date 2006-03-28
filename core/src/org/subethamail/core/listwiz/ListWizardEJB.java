/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.listwiz;

import java.util.List;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.listwiz.i.BlueprintData;
import org.subethamail.core.listwiz.i.ListWizard;
import org.subethamail.core.listwiz.i.ListWizardRemote;
import org.subethamail.core.plugin.PluginRegistry;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.util.Transmute;

/**
 * Implementation of the ListWizard interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="ListWizard")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class ListWizardEJB implements ListWizard, ListWizardRemote
{
	/** */
	private static Log log = LogFactory.getLog(ListWizardEJB.class);
	
	/** */
	@EJB PluginRegistry registry;
	
	/**
	 * @see ListWizard#getBlueprints() 
	 */
	public List<BlueprintData> getBlueprints()
	{
		return Transmute.blueprints(this.registry.getBlueprints());
	}

	/**
	 * @see ListWizard#createMailingList(String, String, String)
	 */
	public Long createMailingList(String address, String url, String blueprintId)
	{
		Blueprint blue = this.registry.getBlueprint(blueprintId);

		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist");
		
		return blue.createMailingList(address, url);
	}
	
	
}

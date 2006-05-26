/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.EJB;
import javax.annotation.security.RolesAllowed;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.admin.i.ListWizardRemote;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.util.Transmute;

/**
 * Implementation of the ListWizard interface.
 * 
 * @author Jeff Schnitzer
 */
@Service(name="ListWizard")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class ListWizardBean implements ListWizard, ListWizardRemote, BlueprintRegistry
{
	/** */
	private static Log log = LogFactory.getLog(ListWizardBean.class);
	
	/** */
	@EJB Admin admin;
	
	/**
	 * Key is blueprint classname.  Watch out for concurrency.
	 */
	Map<String, Blueprint> blueprints = new ConcurrentHashMap<String, Blueprint>();

	/**
	 * @see BlueprintRegistry#register(Blueprint)
	 */
	public void register(Blueprint print)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + print.getClass().getName());
			
		this.blueprints.put(print.getClass().getName(), print);
	}

	/**
	 * @see BlueprintRegistry#deregister(Blueprint)
	 */
	public void deregister(Blueprint print)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + print.getClass().getName());
			
		this.blueprints.remove(print.getClass().getName());
	}
	
	/**
	 * @see ListWizard#getBlueprints() 
	 */
	public List<BlueprintData> getBlueprints()
	{
		return Transmute.blueprints(this.blueprints.values());
	}

	/**
	 * @see ListWizard#createMailingList(InternetAddress, URL, String, InternetAddress[], String)
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, String welcomeMessage, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException
	{
		Blueprint blue = this.blueprints.get(blueprintId);

		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist");
		
		Long listId = this.admin.createMailingList(address, url, description, welcomeMessage, initialOwners);
		
		blue.configureMailingList(listId);
		
		return listId;
	}
	
	
}

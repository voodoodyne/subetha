/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.listwiz;

import java.util.Collection;
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
import org.subethamail.core.admin.i.CreateMailingListException;
import org.subethamail.core.listwiz.i.BlueprintData;
import org.subethamail.core.listwiz.i.ListWizard;
import org.subethamail.core.listwiz.i.ListWizardRemote;
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
	 * @see ListWizard#createMailingList(String, String, Collection, String)
	 */
	public Long createMailingList(String address, String url, Collection<InternetAddress> initialOwners, String blueprintId) throws CreateMailingListException
	{
		Blueprint blue = this.blueprints.get(blueprintId);

		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist");
		
		Long listId = this.admin.createMailingList(address, url, initialOwners);
		
		blue.configureMailingList(listId);
		
		return listId;
	}
	
	
}

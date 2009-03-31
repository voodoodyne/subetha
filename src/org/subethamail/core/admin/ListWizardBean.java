/*
 * $Id: ListWizardBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/ListWizardBean.java $
 */

package org.subethamail.core.admin;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.context.ApplicationScoped;
import javax.inject.Current;
import javax.inject.manager.Manager;
import javax.jws.WebMethod;
import javax.mail.internet.InternetAddress;

import net.sourceforge.stripes.util.ConcurrentHashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.util.Transmute;

/**
 * Implementation of the ListWizard interface.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
public class ListWizardBean implements ListWizard, BlueprintRegistry
{
	/** */
	private static Log log = LogFactory.getLog(ListWizardBean.class);

	/** */
	@Current Admin admin;
	@Current Manager wbManager;
	
	/**
	 * Key is blueprint classname.  Watch out for concurrency.
	 */
	Set<String> blueprints = new ConcurrentHashSet<String>();

	/**
	 * @see BlueprintRegistry#register(Blueprint)
	 */
	public void register(String clazz)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + clazz);

		this.blueprints.add(clazz);
	}

	/**
	 * @see BlueprintRegistry#deregister(Blueprint)
	 */
	public void deregister(String clazz)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + clazz);

		this.blueprints.remove(clazz);
	}

	/**
	 * @see ListWizard#getBlueprints()
	 */
	@WebMethod
	public List<BlueprintData> getBlueprints()
	{
		Collection<Blueprint> bps = new Vector<Blueprint>();
		for(String bp: blueprints)
		{
			bps.add((Blueprint)wbManager.getInstanceByName(bp));
		}
		
		return Transmute.blueprints(bps);
	}

	/**
	 * @see ListWizard#createMailingList(InternetAddress, URL, String, InternetAddress[], String)
	 */
	@WebMethod
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException
	{
		Blueprint blue = (Blueprint)wbManager.getInstanceByName(blueprintId);

//		Blueprint blue = this.blueprints.get(blueprintId);

		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist");

		Long listId = this.admin.createMailingList(address, url, description, initialOwners);

		blue.configureMailingList(listId);

		return listId;
	}
}
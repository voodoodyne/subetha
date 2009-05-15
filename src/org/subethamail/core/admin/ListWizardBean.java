/*
 * $Id: ListWizardBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/ListWizardBean.java $
 */

package org.subethamail.core.admin;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.security.RolesAllowed;
import javax.context.ApplicationScoped;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Current;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.BlueprintData;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.core.admin.i.ListWizard;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;
import org.subethamail.core.util.InjectBeanHelper;
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.Person;

/**
 * Implementation of the ListWizard interface.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */

@ApplicationScoped
@RolesAllowed(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ListWizardBean implements ListWizard, BlueprintRegistry
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ListWizardBean.class);

	/** */
	@Current Admin admin;
	@Current ScannerService ss;

	//@Current 
	InjectBeanHelper<Blueprint> bHelper = new InjectBeanHelper<Blueprint>();

	/**
	 * Key is blueprint classname.  Watch out for concurrency.
	 */
	Map<String,BlueprintData> blueprints = new ConcurrentHashMap<String, BlueprintData>();

	/**
	 * @see BlueprintRegistry#register(Blueprint)
	 */
	public void register(Class<? extends Blueprint> c)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + c.getName());

		BlueprintData bpd = Transmute.blueprint(bHelper.getInstance(c));
		this.blueprints.put(c.getName(), bpd);
	}

	/**
	 * @see BlueprintRegistry#deregister(Blueprint)
	 */
	public void deregister(Class<? extends Blueprint> c)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + c.getName());

		this.blueprints.remove(c.getName());
	}

	/**
	 * @see ListWizard#getBlueprints()
	 */
	public Collection<BlueprintData> getBlueprints()
	{
		return this.blueprints.values();
	}

	/**
	 * @see ListWizard#createMailingList(InternetAddress, URL, String, InternetAddress[], String)
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException
	{
		if(this.blueprints.size() == 0) {ss.scan();}
		
		Blueprint blue = bHelper.getInstance(blueprintId);
		
		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist");

		Long listId = this.admin.createMailingList(address, url, description, initialOwners);

		blue.configureMailingList(listId);

		return listId;
	}
}
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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
	@Inject Admin admin;
	@Inject ScannerService ss;

	@Inject 
	InjectBeanHelper<Blueprint> bHelper = new InjectBeanHelper<Blueprint>();

	/**
	 * Key is blueprint classname.  Watch out for concurrency.
	 */
	Map<String,BlueprintData> blueprints = new ConcurrentHashMap<String, BlueprintData>();
	
	/* */
	public void register(Class<? extends Blueprint> c)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + c.getName());

		BlueprintData bpd = Transmute.blueprint(bHelper.getInstance(c));
		this.blueprints.put(c.getName(), bpd);
	}

	/* */
	public void deregister(Class<? extends Blueprint> c)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + c.getName());

		this.blueprints.remove(c.getName());
	}

	/* */
	public Collection<BlueprintData> getBlueprints()
	{
		return this.blueprints.values();
	}

	/* */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException
	{
		log.debug("Creating ML from " + super.toString() + ", blueprints are: " + this.blueprints);
		
		Blueprint blue=null;
		try {
			blue = bHelper.getInstance(blueprintId);
		} catch (ClassNotFoundException e) {
		}
		
		if (blue == null)
			throw new IllegalStateException("Blueprint does not exist - " + blueprintId);

		Long listId = this.admin.createMailingList(address, url, description, initialOwners);

		blue.configureMailingList(listId);

		return listId;
	}
}
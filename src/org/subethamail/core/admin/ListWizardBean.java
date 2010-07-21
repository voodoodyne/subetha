/*
 * $Id: ListWizardBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/ListWizardBean.java $
 */

package org.subethamail.core.admin;

import java.net.URL;
import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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
import org.subethamail.core.util.Transmute;
import org.subethamail.entity.Person;

import com.caucho.remote.HessianService;

/**
 * Implementation of the ListWizard interface.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
@RolesAllowed(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@HessianService(urlPattern="/api/ListWizard")
public class ListWizardBean implements ListWizard
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ListWizardBean.class);

	/** */
	@Inject Admin admin;
	@Inject @Any Instance<Blueprint> blueprints;

	/* */
	@Override
	public Collection<BlueprintData> getBlueprints()
	{
		return Transmute.blueprints(this.blueprints);
	}

	/* */
	@Override
	@SuppressWarnings("unchecked")
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners, String blueprintId) throws DuplicateListDataException, InvalidListDataException
	{
		log.debug("Creating ML from " + super.toString() + ", blueprints are: " + this.blueprints);
		
		Class<Blueprint> bpClass;
		try
		{
			bpClass = (Class<Blueprint>)Class.forName(blueprintId);
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalStateException("Blueprint does not exist: " + blueprintId);
		}
		
		Blueprint blue = this.blueprints.select(bpClass).get();

		Long listId = this.admin.createMailingList(address, url, description, initialOwners);

		blue.configureMailingList(listId);

		return listId;
	}
}
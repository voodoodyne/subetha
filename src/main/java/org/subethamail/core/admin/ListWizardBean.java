package org.subethamail.core.admin;

import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;

import javax.annotation.security.RolesAllowed;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import lombok.extern.java.Log;

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
@Log
public class ListWizardBean implements ListWizard
{
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
	    if (log.isLoggable(Level.FINE)) log.log(Level.FINE,"Creating ML from {0}, blueprints are: {1}", new Object[]{super.toString(), this.blueprints});
		
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
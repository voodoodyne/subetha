/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import javax.annotation.security.RunAs;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Constructs a trivial list with no frills.
 * 
 * @author Jeff Schnitzer
 */
@Service
//@SecurityDomain("subetha")
//@RunAs("siteAdmin")
public class BarebonesBlueprint extends AbstractBlueprint implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	public String getName()
	{
		return "Barebones List";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a list with no additional configuration.  No plugins" +
			" will be added and the default role will have no permissions.";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		// we're done
	}
}

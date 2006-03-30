/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.plugin.blueprint;

import org.jboss.annotation.ejb.Service;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Constructs a trivial list with no frills.
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha.blueprint:service=SimpleBlueprint")
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
public class SimpleBlueprint extends AbstractBlueprint implements Lifecycle
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
	public Long createMailingList(String address, String url)
	{
		// TODO
		return null;
	}
}

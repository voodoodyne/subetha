/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.plugin.blueprint;

import org.jboss.annotation.ejb.Service;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Creates a list suitable for a small social group. 
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha.blueprint:service=SocialBlueprint")
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
public class SocialBlueprint extends AbstractBlueprint implements Lifecycle
{
	/** */
	public String getName()
	{
		return "Social List";
	}

	/** */
	public String getDescription()
	{
		return 
			"Create a list suitable for social groups.  Subscriptions must" +
			" be approved by moderators but any subscriber may post.  Reply-To" +
			" will be set back to the list.";
	}
	
	/** */
	public void configureMailingList(Long listId)
	{
		// TODO
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.blueprint;

import javax.annotation.security.RunAs;

import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.plugin.i.helper.AbstractBlueprint;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * Creates a list suitable for a small social group. 
 * 
 * @author Jeff Schnitzer
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class SocialBlueprint extends AbstractBlueprint implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
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

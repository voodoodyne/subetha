/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.tagonist.propertize.Property;

/**
 * Removes a site admin.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class AdminRemove extends AuthRequired
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(AdminRemove.class);

	@Property Long id;

	/** */
	@Override
	public void authExecute() throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("Removing site admin person id: " + this.id);

		Backend.instance().getAdmin().setSiteAdmin(this.id, false);
	}
}

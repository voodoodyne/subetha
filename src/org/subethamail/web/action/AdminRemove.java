/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import javax.inject.Current;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.admin.i.Admin;
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
	@Current Admin admin;

	/** */
	private static Log log = LogFactory.getLog(AdminRemove.class);

	@Property Long id;

	/** */
	@Override
	public void authExecute() throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("Removing site admin person id: " + this.id);

		admin.setSiteAdmin(this.id, false);
	}
}

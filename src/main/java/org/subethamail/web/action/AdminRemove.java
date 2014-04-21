/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;

/**
 * Removes a site admin.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class AdminRemove extends AuthRequired
{
	@Getter @Setter Long id;

	/** */
	@Override
	public void authExecute() throws Exception
	{
	    if (log.isLoggable(Level.FINE))
	        log.log(Level.FINE,"Removing site admin person id: {0}", this.id);

		Backend.instance().getAdmin().setSiteAdmin(this.id, false);
	}
}

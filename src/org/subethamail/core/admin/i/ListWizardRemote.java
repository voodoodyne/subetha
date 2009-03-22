/*
 * $Id: ListWizardRemote.java 704 2006-07-31 00:04:03Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/i/ListWizardRemote.java $
 */

package org.subethamail.core.admin.i;

import javax.ejb.Remote;



/**
 * @author Jeff Schnitzer
 */
@Remote
public interface ListWizardRemote extends ListWizard
{
	/** */
	public static final String JNDI_NAME = "subetha/ListWizard/remote";
}

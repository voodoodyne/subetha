/*
 * $Id$
 * $URL$
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

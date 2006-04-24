/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i;

import javax.ejb.Local;


/**
 * A blueprint generates the starting characteristics for
 * a mailing list.  When it executes, it uses the normal EJB
 * interfaces to configure a list with predefined roles, plugins,
 * and configuration.
 *
 * @author Jeff Schnitzer
 */
@Local
public interface Blueprint
{
	/**
	 * @return a nice short name for this blueprint, like "Announce-Only List".
	 */
	public String getName();

	/**
	 * @return a lengthy description of what this blueprint does. 
	 */
	public String getDescription();
	
	/**
	 * Configure a freshly-created mailing list to the specification.
	 */
	public void configureMailingList(Long listId);
}

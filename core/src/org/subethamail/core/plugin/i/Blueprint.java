/*
 * $Id: PersonData.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/blog/i/PersonData.java $
 */

package org.subethamail.core.plugin.i;

import javax.ejb.Local;


/**
 * A blueprint generates the starting characteristics for
 * a mailing list.  When it executes, it uses the normal EJB
 * interfaces to construct a list with predefined roles, plugins,
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
	 * Actually create a mailing list to this blueprint.
	 * 
	 * @return the id of the new list.
	 */
	public Long createMailingList(String address, String url);
}

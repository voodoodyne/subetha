/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi;

import javax.ejb.Local;

/**
 * Interface that plugin factories must implement. 
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface PluginFactory
{
	/**
	 * Gets the list of parameters that this plugin supports.
	 */
	public ParameterDef[] getParameterDefs();
	
	/**
	 * Gets an instance of a plugin to use for execution.
	 */
	public Plugin getPlugin(PluginContext ctx);
}

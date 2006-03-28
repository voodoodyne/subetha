/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.pluginapi.helper;

import javax.annotation.EJB;

import org.subethamail.pluginapi.Plugin;
import org.subethamail.pluginapi.PluginRegistration;

/**
 * Base implementation of a plugin that registers itself upon deployment.
 * Extend this class to automatically have your plugin register itself. 
 * 
 * @author Jeff Schnitzer
 */
abstract public class AbstractPlugin implements Plugin, AbstractPluginManagement
{
	/**
	 * This will automatically be injected by JBoss.
	 */
	@EJB PluginRegistration registry;

	/**
	 * @see AbstractPluginManagement#start()
	 */
	public void start() throws Exception
	{
		this.registry.register(this);
	}
	
	/**
	 * @see AbstractPluginManagement#stop()
	 */
	public void stop()
	{
		this.registry.deregister(this);
	}
}

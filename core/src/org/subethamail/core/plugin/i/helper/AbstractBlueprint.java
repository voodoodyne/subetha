/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import javax.ejb.EJB;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;

/**
 * Base implementation of a blueprint that registers itself upon deployment.
 * Extend this class to automatically have your blueprint register itself. 
 * 
 * @author Jeff Schnitzer
 */
@SecurityDomain("subetha")
//@RunAs("siteAdmin")
abstract public class AbstractBlueprint implements Blueprint, Lifecycle
{
	/**
	 * These will automatically be injected by JBoss.
	 */
	protected @EJB BlueprintRegistry registry;

	/**
	 * @see Lifecycle#start()
	 */
	public void start() throws Exception
	{
		this.registry.register(this);
	}
	
	/**
	 * @see Lifecycle#stop()
	 */
	public void stop()
	{
		this.registry.deregister(this);
	}
}

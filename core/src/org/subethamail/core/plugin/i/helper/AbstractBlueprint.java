/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i.helper;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;

import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.plugin.i.Blueprint;
import org.subethamail.core.plugin.i.BlueprintRegistry;

/**
 * Base implementation of a blueprint that registers itself upon deployment.
 * Extend this class to automatically have your blueprint register itself. 
 * 
 * @author Jeff Schnitzer
 */
@SecurityDomain("subetha")
@RunAs("siteAdmin")
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

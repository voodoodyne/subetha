/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.plugin.i.helper;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterRegistry;

/**
 * Base implementation of a filter that registers itself upon deployment.
 * Extend this class to automatically have your filter register itself. 
 * 
 * @author Jeff Schnitzer
 */
@SecurityDomain("subetha")
@RunAs("siteAdmin")
abstract public class AbstractFilter implements Filter, Lifecycle
{
	/**
	 * This should be injected by JBoss
	 */
	@EJB FilterRegistry registry;

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

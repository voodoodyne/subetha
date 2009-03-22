/*
 * $Id: AbstractFilter.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/plugin/i/helper/AbstractFilter.java $
 */

package org.subethamail.core.plugin.i.helper;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;

import org.jboss.ejb3.annotation.SecurityDomain;
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

/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.core.plugin.i.helper;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.naming.Context;
import javax.naming.InitialContext;

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
		if (this.registry != null)
			throw new RuntimeException("JBoss fixed, code can be removed now");
		else
		{
			Context ctx = new InitialContext();
			this.registry = (FilterRegistry)ctx.lookup("subetha/FilterRunner/local");
		}
		
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

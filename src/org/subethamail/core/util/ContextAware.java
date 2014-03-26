/*
 */

package org.subethamail.core.util;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import lombok.extern.java.Log;

/**
 * This is a cheesey singleton that makes the context path of our application
 * available as a static.  It's a bit of a hack but this information needs to
 * be available in a lot of places that it's inconvenient to use injection.
 * I blame the crappy Servlet spec.
 *
 * @author Jeff Schnitzer
 */
@Singleton
@Startup
@Log
public class ContextAware
{
	/** */
	private static String contextPath;
	
	/** */
	public static String getContextPath() { return contextPath; }
	
	/** */
	@Inject ServletContext ctx;
	
	/** */
	@PostConstruct
	public void startup()
	{
		contextPath = this.ctx.getContextPath();
		
		log.log(Level.FINE,"Application context path is: {0}", contextPath);
	}
}

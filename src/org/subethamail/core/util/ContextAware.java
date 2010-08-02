/*
 */

package org.subethamail.core.util;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ContextAware
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ContextAware.class);

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
		
		log.debug("Application context path is: " + contextPath);
	}
}

/*
 * $Id: VelocityService.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/VelocityService.java $
 */

package org.subethamail.core.util;

import javax.annotation.PostConstruct;

import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.config.Service;

/**
 * This Bean just initializes the static use of Velocity.
 * 
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Service
public class VelocityService
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(VelocityService.class);
	
	/**
	 * Simply initialize the Velocity engine
	 */
	@PostConstruct
	public void start() throws Exception
	{
		try
		{
			Velocity.setProperty(Velocity.RESOURCE_LOADER, "cp");
			Velocity.setProperty("cp.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			Velocity.setProperty("cp.resource.loader.cache", "true");
			Velocity.setProperty("cp.resource.loader.modificationCheckInterval ", "0");
			Velocity.setProperty("input.encoding", "UTF-8");
			Velocity.setProperty("output.encoding", "UTF-8");
			// Very busy servers should increase this value. Default: 20
			// Velocity.setProperty("velocity.pool.size", "20");
			Velocity.init();
			log.debug("Velocity initialized!");
		}
		catch (Exception ex)
		{
			log.error("Unable to initialize Velocity", ex);
			throw new RuntimeException(ex);
		}
	}
}
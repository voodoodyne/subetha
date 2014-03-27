/*
 * $Id: VelocityService.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/VelocityService.java $
 */

package org.subethamail.core.util;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;

import lombok.extern.java.Log;

import org.apache.velocity.app.Velocity;

/**
 * This Bean just initializes the static use of Velocity.
 * 
 * @author Jon Stevens
 * @author Scott Hernandez
 */
@Startup
@Log
public class VelocityService
{
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
			Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.JdkLogChute");
			Velocity.init();
			log.log(Level.FINE,"Velocity initialized!");
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE,"Unable to initialize Velocity", ex);
			throw new RuntimeException(ex);
		}
	}
}
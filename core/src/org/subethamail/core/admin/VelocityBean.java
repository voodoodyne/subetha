/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import javax.annotation.security.RunAs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;

/**
 * This Bean just initializes the static use of Velocity within Subetha.
 * 
 * Note that this bean has neither remote nor local interfaces.
 * 
 * @author Jon Stevens
 */
@Service(objectName="subetha:service=Velocity")
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class VelocityBean implements VelocityManagement
{
	/** */
	private static Log log = LogFactory.getLog(VelocityBean.class);
	
	/**
	 * Simply initialize the Velocity engine
	 */
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
			log.fatal("Unable to initialize Velocity", ex);
			throw new RuntimeException(ex);
		}
	}
}

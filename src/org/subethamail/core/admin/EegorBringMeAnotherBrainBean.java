/*
 * $Id: PlumberBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/PlumberBean.java $
 */

package org.subethamail.core.admin;

import javax.annotation.Named;
import javax.annotation.security.RolesAllowed;
import javax.context.ApplicationScoped;
import javax.inject.Current;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.admin.i.EegorBringMeAnotherBrain;
import org.subethamail.core.post.OutboundMTA;

/**
 * Implements some basic plumbing methods for testing.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
@Named("eegor")
public class EegorBringMeAnotherBrainBean implements EegorBringMeAnotherBrain
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(EegorBringMeAnotherBrainBean.class);

	/** See the javadocs for Brain to see why this silly thing exists */
	@Current Brain brain;
	
	/** TODO: This should be done using a Deployment Descriptors (JSR299) so that 
	 *  the "test" Deployment Descriptor binds to a another mail session 
	 *  on the current test host and port. */
	//@Name("outbound")
	@OutboundMTA
	Session mailSession;
	
	/* (non-Javadoc)
	 * @see Plumber#log(java.lang.String)
	 */
	public void log(String msg)
	{
		log.info(msg);
	}
	
	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void enableTestMode(String mtaHost)
	{
		log.debug("#### Enabling with brain id " + brain.toString());
		
		if (!this.isTestModeEnabled())
		{
			this.brain.mailSmtpHost = this.mailSession.getProperties().getProperty("mail.smtp.host");
			this.brain.mailSmtpPort = this.mailSession.getProperties().getProperty("mail.smtp.port");
		}
		
		// If there was a port, separate the two
		String[] parts = mtaHost.split(":");
		String newHost = parts[0];
		String newPort = (parts.length > 1) ? parts[1] : "25";

		//store old value, and update the overrides
		this.mailSession.getProperties().setProperty("mail.smtp.host", newHost);
		this.mailSession.getProperties().setProperty("mail.smtp.port", newPort);
	}

	/*
	 * (non-Javadoc)
	 */
	@RolesAllowed("siteAdmin")
	public void disableTestMode()
	{
		log.debug("#### Disabling with brain id " + brain.toString());
		
		if (!this.isTestModeEnabled())
		{
			log.warn("Test mode already disabled");
		}
		else
		{
			log.info("Restoring base mail configuration");

			this.mailSession.getProperties().setProperty("mail.smtp.host", this.brain.mailSmtpHost);
			this.mailSession.getProperties().setProperty("mail.smtp.port", this.brain.mailSmtpPort);

			this.brain.mailSmtpHost = null;
			this.brain.mailSmtpPort = null;
		}
	}

	/** */
	public boolean isTestModeEnabled()
	{
		log.debug("##### isTestModeEnabled(), brain id is " + brain.toString());
		
		return this.brain.mailSmtpHost != null;
	}
}

/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RunAs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Mail;
import org.subethamail.entity.SubscriptionHold;

/**
 * Service which wakes up once a night and performs cleanup operations.
 * Old held messages and held subscriptions are pruned.
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=Cleanup")
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class CleanupBean extends EntityManipulatorBean implements CleanupManagement
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CleanupBean.class);
	
	/** Keep held subscriptions around for 30 days */
	public static final long MAX_HELD_SUB_AGE_MILLIS = 1000 * 60 * 60 * 24 * 30;
	
	/** Keep held messages around for 7 days */
	public static final long MAX_HELD_MSG_AGE_MILLIS = 1000 * 60 * 60 * 24 * 7;
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.CleanupManagement#start()
	 */
	public void start() throws Exception
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.CleanupManagement#stop()
	 */
	public void stop() throws Exception
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.CleanupManagement#cleanup()
	 */
	public void cleanup()
	{
		this.cleanupHeldSubscriptions();
		this.cleanupHeldMail();
	}
	
	/**
	 * Purges old subscription holds.
	 */
	protected void cleanupHeldSubscriptions()
	{
		Date cutoff = new Date(System.currentTimeMillis() - MAX_HELD_SUB_AGE_MILLIS);
		
		if (log.isDebugEnabled())
			log.debug("Purging held subscriptions older than " + cutoff);
		
		List<SubscriptionHold> holds = this.em.findHeldSubscriptionsOlderThan(cutoff);
		for (SubscriptionHold hold: holds)
		{
			if (log.isDebugEnabled())
				log.debug("Deleting obsolete hold: " + hold);
			
			this.em.remove(hold);
		}
	}
	
	/**
	 * Purges old held messages.
	 */
	protected void cleanupHeldMail()
	{
		Date cutoff = new Date(System.currentTimeMillis() - MAX_HELD_MSG_AGE_MILLIS);
		
		if (log.isDebugEnabled())
			log.debug("Purging held mail older than " + cutoff);
		
		List<Mail> holds = this.em.findHeldMailOlderThan(cutoff);
		for (Mail hold: holds)
		{
			if (log.isDebugEnabled())
				log.debug("Deleting obsolete hold: " + hold);
			
			this.em.remove(hold);
		}
	}
}

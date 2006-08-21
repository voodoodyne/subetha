/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.security.RunAs;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
@Service(name="Cleanup", objectName="subetha:service=Cleanup")
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
	
	/** */
	class CleanupTask extends TimerTask
	{
		public void run()
		{
			try
			{
				Context ctx = new InitialContext();
				CleanupManagement cleaner = (CleanupManagement)ctx.lookup(CleanupManagement.JNDI_NAME);
				cleaner.cleanup();
			}
			catch (NamingException ex) { throw new RuntimeException(ex); }
		}
	}
	
	/**
	 * Timer used to schedule the service event.
	 */
	Timer timer = new Timer("Cleanup", false);
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.CleanupManagement#start()
	 */
	public void start() throws Exception
	{
		log.info("Starting cleanup service");
		
		// Schedule rebuilds to occur nightly at 4am, plus some random slop time.
		// The slop time makes this play nicer in a clustered environment.  Really
		// this should be a HA singleton service, but that would require running
		// JBoss in a clustered configuration even on a single box.
		Calendar next = Calendar.getInstance();

		next.set(Calendar.HOUR_OF_DAY, 4);
		next.set(Calendar.MINUTE, (int)(60 * Math.random()));
		next.set(Calendar.SECOND, (int)(60 * Math.random()));
		next.set(Calendar.MILLISECOND, (int)(1000 * Math.random()));

		Calendar now = Calendar.getInstance();
		
		if (now.after(next))
			next.add(Calendar.DAY_OF_YEAR, 1);
		
		final long millisInDay = 1000 * 60 * 60 * 24;
		
		this.timer.scheduleAtFixedRate(new CleanupTask(), next.getTime(), millisInDay);
		
		log.info("Cleanup will occur daily starting at " + next.getTime());
	}
	
	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.CleanupManagement#stop()
	 */
	public void stop() throws Exception
	{
		log.info("Stopping cleanup service");
		
		this.timer.cancel();
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
		
		int count = 0;
		
		List<SubscriptionHold> holds = this.em.findHeldSubscriptionsOlderThan(cutoff);
		for (SubscriptionHold hold: holds)
		{
			if (log.isDebugEnabled())
				log.debug("Deleting obsolete hold: " + hold);
			
			this.em.remove(hold);
			count++;
		}
		
		if (count > 0)
			if (log.isInfoEnabled())
				log.info(count + " obsolete subscription holds removed");
	}
	
	/**
	 * Purges old held messages.
	 */
	protected void cleanupHeldMail()
	{
		Date cutoff = new Date(System.currentTimeMillis() - MAX_HELD_MSG_AGE_MILLIS);
		
		if (log.isDebugEnabled())
			log.debug("Purging held mail older than " + cutoff);
		
		int count = 0;
		
		List<Mail> holds = this.em.findHeldMailOlderThan(cutoff);
		for (Mail hold: holds)
		{
			if (log.isDebugEnabled())
				log.debug("Deleting obsolete hold: " + hold);
			
			this.em.remove(hold);

			count++;
		}
		
		if (count > 0)
			if (log.isInfoEnabled())
				log.info(count + " obsolete message holds removed");
	}
}

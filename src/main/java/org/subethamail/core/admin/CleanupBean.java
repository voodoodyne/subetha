package org.subethamail.core.admin;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.java.Log;

import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Mail;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;

import com.caucho.resources.ScheduledTask;

/**
 * Performs cleanup operations by pruning (old) held messages and subscriptions.
 * 
 * This is scheduled (by a {@link ScheduledTask}) daily.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Named("cleanup")
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Stateless
@Log
public class CleanupBean
{
	/** Keep held subscriptions around for 30 days */
	public static final long MAX_HELD_SUB_AGE_MILLIS = 1000L * 60L * 60L * 24L * 30L;

	/** Keep held messages around for 7 days */
	public static final long MAX_HELD_MSG_AGE_MILLIS = 1000L * 60L * 60L * 24L * 7L;

	private static volatile boolean isRunning = false;
	
	/** */
	@Inject @SubEtha SubEthaEntityManager em;

	/**
	 * Cleans up held {@link Subscription}s and {@link Mail}
	 **/
	@Schedule(minute="*/15", hour="*")
	public void cleanup()
	{
	    log.log(Level.FINE,"Starting cleanup");
	    log.log(Level.FINE,"em is {0}", this.em);
		try
		{
			if(!isRunning)
			{
				isRunning = true;
				this.cleanupHeldSubscriptions();
				this.cleanupHeldMail();
			}
			else
			{
				log.warning("Attempted to start cleanup while one is already running; skipping cleanup.");				
			}
		}
		finally 
		{
			isRunning = false;
		}
	}

	/**
	 * Purges old subscription holds.
	 */
	protected void cleanupHeldSubscriptions()
	{
		Date cutoff = new Date(System.currentTimeMillis() - MAX_HELD_SUB_AGE_MILLIS);

		log.log(Level.FINE,"Purging held subscriptions older than {0}", cutoff);

		int count = 0;

		List<SubscriptionHold> holds = this.em.findHeldSubscriptionsOlderThan(cutoff);
		for (SubscriptionHold hold: holds)
		{
		    log.log(Level.FINE,"Deleting obsolete hold: {0}", hold);

			this.em.remove(hold);
			count++;
		}

		if (count > 0)
		    log.log(Level.INFO,"{0} obsolete subscription holds removed with cutoff: {1}", new Object[]{count, cutoff});
	}

	/**
	 * Purges old held messages.
	 */
	protected void cleanupHeldMail()
	{
		Date cutoff = new Date(System.currentTimeMillis() - MAX_HELD_MSG_AGE_MILLIS);

		log.log(Level.FINE,"Purging held mail older than {0}", cutoff);

		int count = 0;

		List<Mail> holds = this.em.findHeldMailOlderThan(cutoff);
		for (Mail hold: holds)
		{
		    log.log(Level.FINE,"Deleting obsolete hold: {0}", hold);

			this.em.remove(hold);

			count++;
		}

		if (count > 0)
		    log.log(Level.INFO,"{0} obsolete message holds removed", count);
	}
}
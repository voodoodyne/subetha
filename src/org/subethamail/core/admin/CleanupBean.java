/*
 * $Id: CleanupBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/CleanupBean.java $
 */

package org.subethamail.core.admin;

import java.util.Date;
import java.util.List;

import javax.annotation.Named;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Mail;
import org.subethamail.entity.SubscriptionHold;

/**
 * Service which wakes up once a night and performs cleanup operations.
 * Old held messages and held subscriptions are pruned.
 *
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Named("cleanupBean")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CleanupBean implements CleanupManagement, Runnable
{
	/** */
	private static Logger log = LoggerFactory.getLogger(CleanupBean.class);

	/** Keep held subscriptions around for 30 days */
	public static final long MAX_HELD_SUB_AGE_MILLIS = 1000L * 60L * 60L * 24L * 30L;

	/** Keep held messages around for 7 days */
	public static final long MAX_HELD_MSG_AGE_MILLIS = 1000L * 60L * 60L * 24L * 7L;

	/** */
	@SubEtha
	protected SubEthaEntityManager em;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		this.cleanup();
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
				log.info(count + " obsolete subscription holds removed with cutoff: " + cutoff);
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
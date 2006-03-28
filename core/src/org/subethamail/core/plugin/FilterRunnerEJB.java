/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.core.plugin;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.dao.DAO;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="FilterRunner")
//@SecurityDomain("subetha")
public class FilterRunnerEJB implements FilterRunner
{
	/** */
	private static Log log = LogFactory.getLog(FilterRunnerEJB.class);

	/** */
	@EJB DAO dao;
	@EJB PluginRegistry registry;

	/**
	 * @see FilterRunner#onInject(MimeMessage, MailingList)
	 */
	public void onInject(MimeMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException
	{
		// TODO:  factor in global plugins
		
		HoldException holdException = null;
		
		for (EnabledFilter enabled: list.getEnabledFilters())
		{
			Filter filter = this.registry.getFilter(enabled.getClassName());
			if (filter == null)
			{
				// Log and ignore
				this.logUnregisteredFilterError(enabled, list);
			}
			else
			{
				FilterContext ctx = new FilterContextImpl(enabled, filter);
				
				try
				{
					filter.onInject(msg, ctx);
				}
				catch (HoldException ex)
				{
					// We only track the first one
					if (holdException == null)
						holdException = ex;
				}
			}
		}
		
		if (holdException != null)
			throw holdException;
	}

	/**
	 * @see FilterRunner#onSendBeforeAttaching(MimeMessage)
	 */
	public void onSendBeforeAttaching(MimeMessage msg, MailingList list) throws IgnoreException
	{
		//TODO
	}

	/**
	 * @see FilterRunner#onSendAfterAttaching(MimeMessage)
	 */
	public void onSendAfterAttaching(MimeMessage msg, MailingList list) throws IgnoreException
	{
		//TODO
	}
	
	/**
	 * Puts a nasty note in the logs when we find a plugin which has been
	 * enabled on a list but is not (or no longer) registered.  It's not
	 * a fatal error; we can just continue and ignore the plugin.
	 */
	protected void logUnregisteredFilterError(EnabledFilter enPlugin, MailingList list)
	{
		if (log.isErrorEnabled())
			log.error("Unregistered filter '" + enPlugin.getClassName() + 
				"' is enabled on list '" + list.getAddress() + "'");
	}
}

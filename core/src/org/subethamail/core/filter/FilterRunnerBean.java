/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterRegistry;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.MailingList;

/**
 * @author Jeff Schnitzer
 */
@Service(name="FilterRunner")

// Security is disabled because of JBoss bug:  http://jira.jboss.org/jira/browse/EJBTHREE-526
// TODO:  re-enable security on this bean when jboss bug fixed.
//@SecurityDomain("subetha")
//@RolesAllowed("siteAdmin")
public class FilterRunnerBean implements FilterRunner, FilterRegistry
{
	/** */
	private static Log log = LogFactory.getLog(FilterRunnerBean.class);

	/**
	 * Key is filter classname.  Make sure we have concurrent access.
	 */
	Map<String, Filter> filters = new ConcurrentHashMap<String, Filter>();

	/**
	 * @see FilterRegistry#register(Filter)
	 */
	public void register(Filter filter)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + filter.getClass().getName());
			
		this.filters.put(filter.getClass().getName(), filter);
	}

	/**
	 * @see FilterRegistry#deregister(Filter)
	 */
	public void deregister(Filter filter)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + filter.getClass().getName());
			
		this.filters.remove(filter.getClass().getName());
	}

	/**
	 * @see FilterRunner#onInject(MimeMessage, MailingList)
	 */
	public void onInject(MimeMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Running onInject filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		// TODO:  factor in global plugins
		
		HoldException holdException = null;
		
		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			Filter filter = this.filters.get(enabled.getClassName());
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
					if (log.isDebugEnabled())
						log.debug("Running filter " + filter);
					
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
				"' is enabled on list '" + list.getEmail() + "'");
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.filter.FilterRunner#getFilters()
	 */
	public Map<String, Filter> getFilters()
	{
		return this.filters;
	}
}

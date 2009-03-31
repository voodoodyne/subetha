/*
 * $Id: FilterRunnerBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/filter/FilterRunnerBean.java $
 */

package org.subethamail.core.filter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Current;
import javax.inject.manager.Manager;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.FilterData;
import org.subethamail.core.plugin.i.ArchiveRenderFilterContext;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterRegistry;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;

/**
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class FilterRunnerBean implements FilterRunner, FilterRegistry
{
	@Current Manager wbManager;
	/** */
	private static Log log = LogFactory.getLog(FilterRunnerBean.class);

	/**
	 * Key is filter classname.  Make sure we have concurrent access.
	 */
	Map<String, FilterData> filters = new ConcurrentHashMap<String, FilterData>();

	/**
	 * @see FilterRegistry#register(Filter)
	 */
	public void register(String filter)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + filter);
		
		this.filters.put(filter,null);
	}

	/**
	 * @see FilterRegistry#deregister(Filter)
	 */
	public void deregister(String filter)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + filter);
			
		this.filters.remove(filter);
	}

	/**
	 * @see FilterRunner#onInject(SubEthaMessage, MailingList)
	 */
	public void onInject(SubEthaMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Running onInject filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		HoldException holdException = null;
		
		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			try
			{
				Filter filter = (Filter)this.wbManager.getInstanceByName(enabled.getClassName());
				if (filter == null)
				{
					// Log and ignore
					this.logUnregisteredFilterError(enabled, list);
				}
				else
				{
					FilterContext ctx = new FilterContextImpl(enabled, filter, msg);
					
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
			catch (Exception e)
			{
				log.error("Error in filter OnInject: ", e);
			}
		}
		
		if (holdException != null)
			throw holdException;
	}

	/**
	 * @see FilterRunner#onSend(SubEthaMessage, Mail)
	 */
	public void onSend(SubEthaMessage msg, Mail mail) throws IgnoreException, MessagingException
	{
		MailingList list = mail.getList();
		
		if (log.isDebugEnabled())
			log.debug("Running onSend filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			try 
			{
				Filter filter = (Filter)this.wbManager.getInstanceByName(enabled.getClassName());
				if (filter == null)
				{
					// Log and ignore
					this.logUnregisteredFilterError(enabled, list);
				}
				else
				{
					SendFilterContext ctx = new SendFilterContextImpl(enabled, filter, msg, mail);
					
					if (log.isDebugEnabled())
						log.debug("Running filter " + filter);
					
					filter.onSend(msg, ctx);
				}
			}			
			catch (Exception e)
			{
				log.error("Error in filter OnSend: ", e);
			}
		}
	}

	/**
	 * @see FilterRunner#onArchiveRender(SubEthaMessage, Mail)
	 */
	public void onArchiveRender(SubEthaMessage msg, Mail mail) throws MessagingException
	{
		MailingList list = mail.getList();
		
		if (log.isDebugEnabled())
			log.debug("Running onArchiveRender filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			try 
			{					
				Filter filter = (Filter)this.wbManager.getInstanceByName(enabled.getClassName());
				if (filter == null)
				{
					// Log and ignore
					this.logUnregisteredFilterError(enabled, list);
				}
				else
				{
					ArchiveRenderFilterContext ctx = new ArchiveRenderFilterContextImpl(enabled, filter, msg, mail);
					
					if (log.isDebugEnabled())
						log.debug("Running filter " + filter);
					
					filter.onArchiveRender(msg, ctx);
				}
			} 
			catch (Exception e) 
			{
				log.error("Error in filter OnArchiveRender: ", e);
			}

		}
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


	/**
	 * @see FilterRegistry#getFilters()
	 */
	public Set<String> getFilters()
	{
		return this.filters.keySet();
	}
}

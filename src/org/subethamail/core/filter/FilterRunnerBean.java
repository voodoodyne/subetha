/*
 * $Id: FilterRunnerBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/filter/FilterRunnerBean.java $
 */

package org.subethamail.core.filter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
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
@ApplicationScoped
public class FilterRunnerBean implements FilterRunner, FilterRegistry
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(FilterRunnerBean.class);

	/** */
	@Inject @Any Instance<Filter> filters;

	/* */
	@Override
	public Iterable<Filter> getFilters()
	{
		return this.filters;
	}

	/* */
	@Override
	public void onInject(SubEthaMessage msg, MailingList list) throws IgnoreException, HoldException, MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Running onInject filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		HoldException holdException = null;
		
		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			Filter filter = this.getFilterFor(enabled);
			if (filter != null)
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
		
		if (holdException != null)
			throw holdException;
	}

	/* */
	public void onSend(SubEthaMessage msg, Mail mail) throws IgnoreException, MessagingException
	{
		MailingList list = mail.getList();
		
		if (log.isDebugEnabled())
			log.debug("Running onSend filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			Filter filter = this.getFilterFor(enabled);
			if (filter != null)
			{
				SendFilterContext ctx = new SendFilterContextImpl(enabled, filter, msg, mail);
				
				if (log.isDebugEnabled())
					log.debug("Running filter " + filter);
				
				filter.onSend(msg, ctx);
			}
		}
	}

	/* */
	public void onArchiveRender(SubEthaMessage msg, Mail mail) throws MessagingException
	{
		MailingList list = mail.getList();
		
		if (log.isDebugEnabled())
			log.debug("Running onArchiveRender filters for list '" + list.getName() + "' on message: " + msg.getSubject());

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			try 
			{					
				Filter filter = this.getFilterFor(enabled);
				if (filter != null)
				{
					ArchiveRenderFilterContext ctx = new ArchiveRenderFilterContextImpl(enabled, filter, msg, mail);
					
					if (log.isDebugEnabled())
						log.debug("Running filter " + filter);
					
					filter.onArchiveRender(msg, ctx);
				}
			} 
			catch (Exception e) 
			{
				log.error("Error in filter OnArchiveRender", e);
			}
		}
	}
	
	/**
	 * @return null if couldn't get it for some reason (logs error too)
	 */
	private Filter getFilterFor(EnabledFilter enabled)
	{
		try
		{
			return this.getFilter(enabled.getClassName());
		}
		catch (Exception ex)
		{
			if (log.isErrorEnabled())
				log.error("Problem with filter '" + enabled.getClassName() + 
					"' on list '" + enabled.getList().getEmail() + "'", ex);
			
			return null;
		}
	}

	/* */
	@Override
	@SuppressWarnings("unchecked")
	public Filter getFilter(String filterClassName)
	{
		Class<Filter> filterClass;
		try
		{
			filterClass = (Class<Filter>)Class.forName(filterClassName);
		}
		catch (ClassNotFoundException e) { throw new RuntimeException(e); }
		
		return this.filters.select(filterClass).get();
	}
}

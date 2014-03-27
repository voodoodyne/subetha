/*
 * $Id: FilterRunnerBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/filter/FilterRunnerBean.java $
 */

package org.subethamail.core.filter;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.MessagingException;

import lombok.extern.java.Log;

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
@Log
public class FilterRunnerBean implements FilterRunner, FilterRegistry
{
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
	    log.log(Level.FINE,"Running onInject filters for list ''{0}'' on message: {1}", new Object[]{list.getName(), msg.getSubject()});

		HoldException holdException = null;
		
		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			Filter filter = this.getFilterFor(enabled);
			if (filter != null)
			{
				FilterContext ctx = new FilterContextImpl(enabled, filter, msg);
				
				try
				{
				    log.log(Level.FINE,"Running filter {0}");
					
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
		
		log.log(Level.FINE,"Running onSend filters for list ''{0}'' on message: {1}", new Object[]{list.getName(), msg.getSubject()});

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			Filter filter = this.getFilterFor(enabled);
			if (filter != null)
			{
				SendFilterContext ctx = new SendFilterContextImpl(enabled, filter, msg, mail);
				
				log.log(Level.FINE,"Running filter {0}", filter);
				
				filter.onSend(msg, ctx);
			}
		}
	}

	/* */
	public void onArchiveRender(SubEthaMessage msg, Mail mail) throws MessagingException
	{
		MailingList list = mail.getList();
		
		log.log(Level.FINE,"Running onArchiveRender filters for list ''{0}'' on message: {1}", new Object[]{list.getName(), msg.getSubject()});

		for (EnabledFilter enabled: list.getEnabledFilters().values())
		{
			try 
			{					
				Filter filter = this.getFilterFor(enabled);
				if (filter != null)
				{
					ArchiveRenderFilterContext ctx = new ArchiveRenderFilterContextImpl(enabled, filter, msg, mail);
					
					log.log(Level.FINE,"Running filter {0}", filter);
					
					filter.onArchiveRender(msg, ctx);
				}
			} 
			catch (Exception e) 
			{
			    log.log(Level.SEVERE,"Error in filter OnArchiveRender", e);
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
			LogRecord logRecord=new LogRecord(Level.SEVERE, "Problem with filter ''{0}'' on list ''{1}''");
			logRecord.setParameters(new Object[]{enabled.getClassName(), enabled.getList().getEmail()});
			logRecord.setThrown(ex);
			log.log(logRecord);
			
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

/*
 * $Id: SendFilterContextImpl.java 902 2007-01-15 03:00:15Z skot $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/filter/SendFilterContextImpl.java $
 */

package org.subethamail.core.filter;

import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.MailSummary;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.Mail;


/**
 * Implementation of the SendFilterContext
 * 
 * @author Jeff Schnitzer
 * @author Jon Stevens
 */
class SendFilterContextImpl extends FilterContextImpl implements SendFilterContext
{
	/** The mail object associated with the SubEthaMessage */
	Mail mail;
	
	/** Cached just in case the client calls lots of times */
	MailSummary cachedThreadRoot;
	
	/** 
	 */
	public SendFilterContextImpl(EnabledFilter enabledFilter, Filter filter, SubEthaMessage msg, Mail mail)
	{
		super(enabledFilter, filter, msg);
		
		this.mail = mail;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.FilterContext#getMailId()
	 */
	public Long getMailId()
	{
		return this.mail.getId();
	}
}
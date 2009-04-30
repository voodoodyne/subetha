/*
 * $Id: SendFilterContextImpl.java 263 2006-05-04 20:58:25Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/filter/SendFilterContextImpl.java $
 */

package org.subethamail.core.filter;

import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.ArchiveRenderFilterContext;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.entity.EnabledFilter;
import org.subethamail.entity.Mail;


/**
 * Implementation of the SendFilterContext
 * 
 * @author Scott Hernandez
 */
class ArchiveRenderFilterContextImpl extends FilterContextImpl implements ArchiveRenderFilterContext
{
	/** The mail object associated with the SubEthaMessage */
	Mail mail;
	
	/** 
	 */
	public ArchiveRenderFilterContextImpl(EnabledFilter enabledFilter, Filter filter, SubEthaMessage msg, Mail mail)
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
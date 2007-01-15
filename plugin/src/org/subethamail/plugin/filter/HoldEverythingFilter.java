/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import javax.annotation.security.RunAs;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.ArchiveRenderFilterContext;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter holds all mail no matter what.  This is useful for large
 * announce-only lists; since mail from the list owners could be spoofed,
 * this is the only way to absolutely prevent abuse.
 * 
 * This is likely the simplest possible filter that does something useful.
 * 
 * @author Jeff Schnitzer
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class HoldEverythingFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{	
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(HoldEverythingFilter.class);

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Hold All Mail";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Holds all messages for manual approval.  Especially useful when a list" +
				" is (or is likely to be) abused by spoofed email.";
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.helper.GenericFilter#onInject(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.FilterContext)
	 */
	@Override
	public void onInject(SubEthaMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
		throw new HoldException("Holding all messages");
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.helper.GenericFilter#onArchiveRender(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.ArchiveRenderFilterContext)
	 */
	@Override
	public void onArchiveRender(SubEthaMessage msg, ArchiveRenderFilterContext ctx) throws MessagingException
	{
	}
}

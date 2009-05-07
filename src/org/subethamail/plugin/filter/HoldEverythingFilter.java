/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.helper.GenericFilter;

/**
 * This filter holds all mail no matter what.  This is useful for large
 * announce-only lists; since mail from the list owners could be spoofed,
 * this is the only way to absolutely prevent abuse.
 * 
 * This is likely the simplest possible filter that does something useful.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class HoldEverythingFilter extends GenericFilter 
{	
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(HoldEverythingFilter.class);

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
}

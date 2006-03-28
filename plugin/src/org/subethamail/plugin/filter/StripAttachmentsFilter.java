/*
 * $Id: Transmute.java 105 2006-02-27 10:06:27Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/Transmute.java $
 */

package org.subethamail.plugin.filter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.helper.FilterParameterImpl;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter removes all attachments greater than a certain size
 * immediately upon message injection.  The attachments are never
 * stored.  The attachment can optionally be replaced with a message
 * indicating what action was taken.  
 * 
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha.filter:service=StripAttachmentsFilter")
// TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
public class StripAttachmentsFilter extends GenericFilter implements Lifecycle
{
	/** */
	private static Log log = LogFactory.getLog(StripAttachmentsFilter.class);
	
	/** */
	static FilterParameter[] PARAM_DEFS = new FilterParameter[] {
		new FilterParameterImpl(
				"Threshold in K",
				"Strip all attachments larger than this size, in kilobytes.  A value of 0 will strip all attachments.",
				Long.class,
				100
			)
	};

	/**
	 * @see PluginFactory#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		return PARAM_DEFS;
	}

	/**
	 * @see Filter#onInject(MimeMessage, FilterContext)
	 */
	@Override
	public void onInject(MimeMessage msg, FilterContext ctx) throws IgnoreException, HoldException, MessagingException
	{
		// TODO:  implement this.
		log.debug("onInject()");
	}
}

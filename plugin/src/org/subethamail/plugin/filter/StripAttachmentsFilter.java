/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import java.io.IOException;
import javax.annotation.security.RunAs;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
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
 * @author Scott Hernandez
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class StripAttachmentsFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	private static Log log = LogFactory.getLog(StripAttachmentsFilter.class);
	
	public static final String ARG_MAXSIZEINKB = "Threshold in KB";

	public static final String ARG_MSG = "Replace Message";
	
	
	private static final String DEFAULT_MSG = 
		"\n_______________________________________________\n" +
		"${list.name} mailing list\n" +
		"${list.email}\n" +
		"${list.url}";
	
	/** */
	static FilterParameter[] PARAM_DEFS = new FilterParameter[] {
		new FilterParameterImpl(
				ARG_MAXSIZEINKB,
				"Strip all attachments larger than this size, in kilobytes.  A value of 0 will strip all attachments.",
				Integer.class,
				100
			),
			new FilterParameterImpl(
					ARG_MSG,
					"The message text which replaces the attachements.",
					DEFAULT_MSG,
					20,
					true,
					null
				)
	};

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Strip Attachments";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Eliminates attachments larger than a certain size.";
	}
	
	/**
	 * @see PluginFactory#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		return PARAM_DEFS;
	}

	/**
	 * @see Filter#onInject(SubEthaMessage, FilterContext)
	 */
	@Override
	public void onInject(SubEthaMessage msg, FilterContext ctx)
		throws IgnoreException, HoldException, MessagingException
	{

		int maxKB = Integer.parseInt(ctx.getArgument(ARG_MAXSIZEINKB).toString());

		String msgContent = (String) ctx.getArgument(ARG_MSG);
		String expandedMsg = ctx.expand(msgContent);
 
		try 
		{
			for (Part p : msg.getParts())
			{
				if (p.getSize() > (maxKB * 1024)) 
				{
					if (log.isDebugEnabled())
						log.debug("Stripping attachement of type: " + p.getContentType());		
	
					p.setText(expandedMsg);		
				}
			}
		}
		catch (IOException ioex)
		{
			if (log.isDebugEnabled())
				log.debug("Error getting message parts" + ioex);					
		}
	}
}
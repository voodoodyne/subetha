/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.HoldException;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.helper.FilterParameterImpl;
import org.subethamail.core.plugin.i.helper.GenericFilter;

/**
 * This filter removes all attachments greater than a certain size
 * immediately upon message injection.  The attachments are never
 * stored.  The attachment can optionally be replaced with a message
 * indicating what action was taken.  
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
public class StripAttachmentsFilter extends GenericFilter
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(StripAttachmentsFilter.class);
	
	public static final String ARG_MAXSIZEINKB = "Threshold in KB";

	public static final String ARG_MSG = "Replace Message";
	
	
	private static final String DEFAULT_MSG = 
		"\n Attachment Removed";
	
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
		return "Eliminates attachments larger than a certain size on incoming mail. These attachments will be lost forever.";
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
	@SuppressWarnings("unchecked")
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

					//remove all headers
					for (Enumeration<Header> e = p.getAllHeaders(); e.hasMoreElements();)
					{
						Header header = e.nextElement();
						p.removeHeader(header.getName());
					}
	
					p.setText(expandedMsg);		
				}
			}
			msg.save();
		}
		catch (IOException ioex)
		{
			if (log.isDebugEnabled())
				log.debug("Error getting message parts" + ioex);					
		}
	}
}
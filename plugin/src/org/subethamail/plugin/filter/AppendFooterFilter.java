/*
 * $Id$
 * $URL$
 */

package org.subethamail.plugin.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RunAs;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.core.plugin.i.IgnoreException;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.FilterParameterImpl;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter appends a customizable footer to the bottom
 * of outgoing emails.
 * 
 * @author Jon Stevens
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class AppendFooterFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	private static Log log = LogFactory.getLog(AppendFooterFilter.class);
	
	private static final String ARG_FOOTER = "Footer";
	
	private static final String ARCHIVE_MESSAGE_URL = "archive.message.url";

	private static final String DEFAULT_FOOTER = 
		"_______________________________________________\n" +
		"${list.name} mailing list\n" +
		"${list.email}\n" +
		"${list.url}\n" +
		"${" + ARCHIVE_MESSAGE_URL + "}";


	/** */
	static FilterParameter[] PARAM_DEFS;

	static
	{
		Map<String, String> documentation = 
			Collections.singletonMap("${" + ARCHIVE_MESSAGE_URL + "}", 
				"Absolute URL link to the message in the archives.");

		PARAM_DEFS = new FilterParameter[] {
				new FilterParameterImpl(
						ARG_FOOTER,
						"The footer text which is appended to the bottom of the email body.",
						DEFAULT_FOOTER,
						20,
						true,
						documentation
					)
			};
	}

	/**
	 * @see Lifecycle#start()
	 */
	public void start() throws Exception
	{
		super.start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Append Footer";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Text which is appended to the bottom of each message to the list.";
	}
	
	/**
	 * @see PluginFactory#getParameters()
	 */
	public FilterParameter[] getParameters()
	{
		return PARAM_DEFS;
	}

	/**
	 * @see Filter#onSend(SubEthaMessage, SendFilterContext)
	 */
	@Override
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws IgnoreException, MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("AppendFooterFilter: onSend()");
		
		try
		{
			String footerContent = (String) ctx.getArgument(ARG_FOOTER);
			
			// Add a link to the message in the archive into the context.
			Map<String, Object> map = new HashMap<String, Object>();
			String archiveUrl = ctx.getList().getUrlBase() + "archive_msg.jsp?msgId=" + ctx.getMailId();
			map.put(ARCHIVE_MESSAGE_URL, archiveUrl);

			String expandedFooter = "\n" + ctx.expand(footerContent, map);

			String contentType = msg.getContentType().toLowerCase();
			
			if (contentType.startsWith("text/plain"))
			{
				String text = (String)msg.getContent();
				msg.setText(text + expandedFooter);
			}
			else if (contentType.startsWith("multipart/"))
			{
				MimeMultipart multi = (MimeMultipart)msg.getContent();

				MimeBodyPart part = new MimeBodyPart();
				part.setText(expandedFooter);
				multi.addBodyPart(part);
				
				msg.save();
			}
			else
			{
				if (log.isDebugEnabled())
					log.debug("Can't append a footer for type " + contentType);
			}
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}

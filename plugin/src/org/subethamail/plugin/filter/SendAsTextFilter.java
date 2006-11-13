/*
 * $Id: StripAttachmentsFilter.java 415 2006-05-19 05:57:40Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import java.io.IOException;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter removes all attachments greater than a certain size immediately
 * upon message injection. The attachments are never stored. The attachment can
 * optionally be replaced with a message indicating what action was taken.
 * 
 * @author Scott Hernandez
 */

@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
// TODO: remove the implements clause when
// http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
public class SendAsTextFilter extends GenericFilter implements Lifecycle
{
	/** */
	private static Log log = LogFactory.getLog(SendAsTextFilter.class);

	@EJB Archiver archiver;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Send Email as Text Only";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Tries to send email as text only. This will strip everything that isn't text.";
	}

	/**
	 * @see Filter#onSend(SubEthaMessage, FilterContext)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws MessagingException
	{
		String archiveUrl = ctx.getList().getUrlBase() + "archive_msg.jsp?msgId=" + ctx.getMailId();
		String archiveFooter = "This message can be read on the Archives: " + archiveUrl;
		try
		{
			String msgText = null;
			if(msg.getParts().size() > 0) 
			{
				MimeMultipart multi = (MimeMultipart)msg.getContent();
				for (int i = 0; i < multi.getCount(); i++)
				{
					BodyPart bp = multi.getBodyPart(i);
					if (bp.getContentType().toLowerCase().startsWith("text")) 
					{
						msgText = (String)bp.getContent();
						break;
					}
				}
			}
			
			if (msgText != null) 
			{
				msg.setText(msgText + "\r\n" + archiveFooter);
			} 
			else 
			{
				msg.setText(archiveFooter);
			}
		}
		catch (IOException ioex)
		{
			if (log.isDebugEnabled())
				log.debug("Error getting message parts", ioex);
		}		
		return;
	}
}
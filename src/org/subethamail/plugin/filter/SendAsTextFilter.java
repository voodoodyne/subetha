/*
 * $Id: StripAttachmentsFilter.java 415 2006-05-19 05:57:40Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import java.io.IOException;

import javax.inject.Current;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.GenericFilter;

/**
 * This filter converts every message to simple plain text, although it leaves
 * attachments intact.  If the message is multipart/alternative, the HTML version
 * is removed and the plain is left as the master piece.  If the message is text/html,
 * the body is replaced with a plain link to the archives.
 *
 * @author Scott Hernandez
 * @author Jeff Schnitzer
 */
public class SendAsTextFilter extends GenericFilter
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(SendAsTextFilter.class);

	@Current Archiver archiver;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Text Only";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Send email as text only. This will strip everything but the first text/plain part. If no text part is found, a link to the archives is sent in the place of the message.";
	}

	/**
	 * @see Filter#onSend(SubEthaMessage, FilterContext)
	 */
	@Override
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws MessagingException
	{
		String archiveUrl = ctx.getList().getUrlBase() + "archive_msg.jsp?msgId=" + ctx.getMailId();
		String archiveFooter = "Parts of this message have been removed. The full message can be read in the Archives: " + archiveUrl;
		try
		{
			String msgText = null;
			if(msg.getParts().size() > 0)
			{
				MimeMultipart multi = (MimeMultipart)msg.getContent();
				for (int i = 0; i < multi.getCount(); i++)
				{
					BodyPart bp = multi.getBodyPart(i);
					if (bp.getContentType().toLowerCase().startsWith("text/plain"))
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
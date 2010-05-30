/*
 * $Id: StripAttachmentsFilter.java 415 2006-05-19 05:57:40Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.MailUtils;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.GenericFilter;

/**
 * SubEtha actually detaches all binary attachments upon message injection and
 * replaces them in the message with a special reference.  Then, when the message
 * is sent outbound, the attachments are reattached.  This filter changes this
 * behavior; instead of reattaching the attachment, a link to the archives is
 * placed there instead.
 * 
 * @author Scott Hernandez
 * @author Jeff Schnitzer
 */
@Singleton
public class LeaveAttachmentsOnServerFilter extends GenericFilter
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(LeaveAttachmentsOnServerFilter.class);

	@Inject Archiver archiver;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Leave Attachments on Server";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Replace attachments in messages with a URL to a download page in the archives.";
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.helper.GenericFilter#onSend(org.subethamail.common.SubEthaMessage, org.subethamail.core.plugin.i.SendFilterContext)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws MessagingException
	{
		try
		{
			boolean didSomething = false;
			
			for (Part part: msg.getParts())
			{
				if (part.getContentType().startsWith(SubEthaMessage.DETACHMENT_MIME_TYPE))
				{
					Long id = (Long)part.getContent();
					String contentType = part.getHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE)[0];
					
					// remove all headers
					for (Enumeration<Header> e = part.getAllHeaders(); e.hasMoreElements();)
					{
						Header header = e.nextElement();
						part.removeHeader(header.getName());
					}
					
					String name = MailUtils.getNameFromContentType(contentType);
					String attachmentUrl = ctx.getList().getUrlBase() + "attachment/" + id + "/" + name ;
					
					part.setText("This attachment was left behind at the server:\n     " + attachmentUrl + "\n");
					part.setDisposition(Part.INLINE);
					didSomething = true;
				}
			}
			
			if (didSomething) msg.save();
		}
		catch (IOException ioex)
		{
			if (log.isDebugEnabled())
				log.debug("Error getting message parts", ioex);
		}
	}
}
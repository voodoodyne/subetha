/*
 * $Id: StripAttachmentsFilter.java 415 2006-05-19 05:57:40Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import java.io.IOException;

import javax.annotation.EJB;
import javax.annotation.security.RunAs;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.MailUtils;
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
public class LeaveAttachmentsOnServerFilter extends GenericFilter implements Lifecycle
{
	/** */
	private static Log log = LogFactory.getLog(LeaveAttachmentsOnServerFilter.class);

	@EJB
	Archiver archiver;

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

	/**
	 * @see Filter#onSend(SubEthaMessage, FilterContext)
	 */
	@Override
	public void onSend(SubEthaMessage msg, SendFilterContext ctx) throws MessagingException
	{
		try
		{
			for (Part part: msg.getParts())
			{
				if (part.getContentType().startsWith(SubEthaMessage.DETACHMENT_MIME_TYPE))
				{
					Long id = (Long)part.getContent();
					String contentType = part.getHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE)[0];
					
					String name = MailUtils.getNameFromContentType(contentType);
					String attachmentUrl = ctx.getList().getUrlBase() + "attachment/" + id + "/" + name ;
					part.setText("This attachment was left behind at the server:\n\n" + attachmentUrl);
					part.removeHeader(SubEthaMessage.HDR_CONTENT_DISPOSITION);
				}

				part.removeHeader(SubEthaMessage.HDR_ORIGINAL_CONTENT_TYPE);
			}
			
			msg.save();
		}
		catch (IOException ioex)
		{
			if (log.isDebugEnabled())
				log.debug("Error getting message parts", ioex);
		}
	}
}
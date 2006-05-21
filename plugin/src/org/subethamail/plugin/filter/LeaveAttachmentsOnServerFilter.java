/*
 * $Id: StripAttachmentsFilter.java 415 2006-05-19 05:57:40Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/plugin/src/org/subethamail/plugin/filter/StripAttachmentsFilter.java $
 */

package org.subethamail.plugin.filter;

import java.io.IOException;

import javax.annotation.security.RunAs;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.SubEthaMessage;
import org.subethamail.core.plugin.i.Filter;
import org.subethamail.core.plugin.i.FilterContext;
import org.subethamail.core.plugin.i.SendFilterContext;
import org.subethamail.core.plugin.i.helper.GenericFilter;
import org.subethamail.core.plugin.i.helper.Lifecycle;

/**
 * This filter removes all attachments greater than a certain size
 * immediately upon message injection.  The attachments are never
 * stored.  The attachment can optionally be replaced with a message
 * indicating what action was taken.  
 * 
 * @author Scott Hernandez
 */
@Service
@SecurityDomain("subetha")
@RunAs("siteAdmin")
public class LeaveAttachmentsOnServerFilter extends GenericFilter implements Lifecycle
//TODO:  remove the implements clause when http://jira.jboss.org/jira/browse/EJBTHREE-489 is fixed
{
	/** */
	private static Log log = LogFactory.getLog(LeaveAttachmentsOnServerFilter.class);
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getName()
	 */
	public String getName()
	{
		return "Leave attachments on server";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.plugin.i.Filter#getDescription()
	 */
	public String getDescription()
	{
		return "Send out links to the attachments instead of including them.";
	}
	
	/**
	 * @see Filter#onSend(SubEthaMessage, FilterContext)
	 */
	@Override
	public void onSend(SubEthaMessage msg, SendFilterContext ctx)
		throws MessagingException
	{

		try 
		{
			for (Part p : msg.getParts())
			{
				// Look for special header which means we must reattach.
				String[] headers = p.getHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
				if (headers != null && headers.length > 0)
				{
					//TODO: Added attachment name in here after the id, like /attachment/id/name
					p.setText("<a href=\"" + ctx.getList().getUrlBase() + "attachement/" + headers[0] +  "\"> download attachement "  + headers[0] +  "</a>");
					p.removeHeader(SubEthaMessage.HDR_ATTACHMENT_REF);
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
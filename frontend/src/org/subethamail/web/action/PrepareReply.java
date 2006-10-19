/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.MailUtils;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.web.Backend;
import org.tagonist.propertize.Property;

/**
 * This action is used primarily on msg_send.jsp to initialize the data for that page.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class PrepareReply extends PostMessage
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(PrepareReply.class);

	public class Model extends PostMessage.Model
	{
		@Property MailData mailData;
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void authExecute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		log.debug("msgId: " + model.msgId);

		if (model.msgId != null && model.msgId.longValue() > 0)
		{
			model.mailData = Backend.instance().getArchiver().getMail(model.msgId);
			model.subject = MailUtils.cleanRe(model.mailData.getSubject(), null, true);
			model.listId = model.mailData.getListId();
		}
	}
}

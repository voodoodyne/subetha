/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.subethamail.common.MailUtils;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.web.Backend;

/**
 * This action is used primarily on msg_send.jsp to initialize the data for that page.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class PrepareReply extends PostMessage
{
	public class Model extends PostMessage.Model
	{
		@Getter @Setter MailData mailData;
	}

	/** */
	@Override
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}

	/** */
	@Override
	public void authExecute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();

		log.log(Level.FINE,"msgId: {0}", model.msgId);

		if (model.msgId != null && model.msgId.longValue() > 0)
		{
			model.mailData = Backend.instance().getArchiver().getMail(model.msgId);
			model.subject = MailUtils.cleanRe(model.mailData.getSubject(), null, true);
			model.listId = model.mailData.getListId();
		}
	}
}

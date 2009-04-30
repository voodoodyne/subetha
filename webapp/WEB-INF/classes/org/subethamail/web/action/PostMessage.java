/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Injects a piece of mail into the system via a web form.  One of msgId or listId
 * will be specified to indicate reply or post, respectively.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class PostMessage extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(PostMessage.class);

	public class Model extends ErrorMapModel
	{
		@Property Long msgId;
		@Property Long listId;

		@Length(min=1, max=Validator.MAX_MAIL_SUBJECT)
		@Property String subject;

		@Length(min=1, max=Validator.MAX_MAIL_CONTENT)
		@Property String message;
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
		
		model.validate();

		if (model.getErrors().isEmpty())
		{
			// Reply
			if (model.msgId != null)
			{
				model.listId = Backend.instance().getArchiver().reply(this.getAuthName(), model.msgId, model.subject, model.message);
			}
			else
			{
				Backend.instance().getArchiver().post(this.getAuthName(), model.listId, model.subject, model.message);
			}
		}
	}
}

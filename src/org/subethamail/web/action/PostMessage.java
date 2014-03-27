/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import org.hibernate.validator.constraints.Length;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;

/**
 * Injects a piece of mail into the system via a web form.  One of msgId or listId
 * will be specified to indicate reply or post, respectively.
 * 
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Log
public class PostMessage extends AuthRequired 
{
	public class Model extends ErrorMapModel
	{
		@Getter @Setter Long msgId;
		@Getter @Setter Long listId;

		@Length(min=1, max=Validator.MAX_MAIL_SUBJECT)
		@Getter @Setter String subject;

		@Length(min=1, max=Validator.MAX_MAIL_CONTENT)
		@Getter @Setter String message;
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

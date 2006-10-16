/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;
import org.subethamail.core.lists.i.MailData;
import org.subethamail.entity.i.Validator;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Injects a piece of mail into the system via a web form.
 * 
 * @author Jon Stevens
 */
public class InjectMessage extends AuthRequired 
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(InjectMessage.class);

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
				MailData mail = Backend.instance().getArchiver().getMail(model.msgId);
				if (log.isDebugEnabled())
					log.debug("Injecting message from " + this.getAuthName() + " to list: " + mail.getListId());
	
				model.listId = mail.getListId();
				
				Backend.instance().getInjector().inject(this.getAuthName(), model.listId, model.msgId, model.subject, model.message);
			}
			else if (model.listId != null)	// Post
			{
				Backend.instance().getInjector().inject(this.getAuthName(), model.listId, null, model.subject, model.message);
			}
		}
	}
}

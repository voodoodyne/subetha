/*
 * $Id: Login.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/Login.java $
 */

package org.subethamail.web.action;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Email;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.web.action.auth.AuthRequired;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Injects mail into the system.  The model starts and remains an Inject.Model.
 * 
 * @author Jeff Schnitzer
 */
public class Inject extends AuthRequired 
{
	/** */
	private static Log log = LogFactory.getLog(Inject.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** */
		@Email
		@Property String to = "";
	
		/** */
		@Property String body = "";
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

		// Basic validation
		model.validate();
		
		// Maybe we can proceed?
		if (model.getErrors().isEmpty())
		{
			Context ctx = new InitialContext();
			Injector injector = (Injector)ctx.lookup(Injector.JNDI_NAME);
			
			injector.inject(model.to, model.body.getBytes());
		}
	}
	
}

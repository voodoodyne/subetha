/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Current;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.core.acct.i.AccountMgr;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.web.security.ResinLogin;

/**
 * Servlet allows calling the injector with a very simple HTTP POST
 * rather than going through the SOAP stack.
 * 
 * Parameters expected:
 * 
 * authEmail: the email address of a site administrator
 * authPassword: the password for the site administrator account
 * from: the email address of the envelope sender
 * recipient: the email address of the envelope recipient
 * message: the rfc822 content of the message
 * 
 * The result will be 200 OK if accepted, 500 if an error occurred.
 * A 599 will be returned if the recipient is unknown.
 * 
 * TODO:  make this a lot more efficient by using the content body
 * as the raw message bytes instead of requiring www-form-urlencoded 
 */
@SuppressWarnings("serial")
public class InjectorServlet extends HttpServlet
{
	@Current AccountMgr accMgr;
	@Current Injector inj;
	
	@Current ResinLogin resinLogin;
	
	/** */
	public static final String AUTH_EMAIL_PARAM = "authEmail";
	public static final String AUTH_PASS_PARAM = "authPassword";
	public static final String FROM_PARAM = "from";
	public static final String RECIPIENT_PARAM = "recipient";
	public static final String MESSAGE_PARAM = "message";
	
	/**
	 * Status code we return when we don't know what to do with the recipient.
	 */
	public static final int SC_ADDRESS_UNKNOWN = 599;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String authEmail = request.getParameter(AUTH_EMAIL_PARAM);
		String authPass = request.getParameter(AUTH_PASS_PARAM);
		String from = request.getParameter(FROM_PARAM);
		String recipient = request.getParameter(RECIPIENT_PARAM);
		String message = request.getParameter(MESSAGE_PARAM);
		
		if (authEmail == null || authPass == null || from == null || recipient == null || message == null)
			throw new ServletException("Missing parameter");
		
		try
		{
			if (!this.resinLogin.login(authEmail, authPass, request))
				throw new ServletException("Bad email or password");
			
			if (!inj.inject(from, recipient, new ByteArrayInputStream(message.getBytes())))
				response.sendError(SC_ADDRESS_UNKNOWN, "Recipient address unknown");
		}
		finally
		{
			this.resinLogin.logout(request);
		}
	}
}

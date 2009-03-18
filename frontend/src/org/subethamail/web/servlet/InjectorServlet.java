/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Current;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.core.acct.i.AuthCredentials;
import org.subethamail.core.acct.i.LoginStatus;
import org.subethamail.web.Backend;

/**
 * Servlet allows calling the injector with a very simple HTTP POST
 * rather than going through the SOAP stack.
 * 
 * Parameters expected:
 * 
 * authId: the person id of a site administrator (this *or* authName must be specified)
 * authName: the email address of a site administrator (this *or* authId must be specified)
 * authPassword: the password for the site administrator account
 * from: the email address of the envelope sender
 * recipient: the email address of the envelope recipient
 * message: the rfc822 content of the message
 * 
 * The result will be 200 OK if accepted, 500 if an error occurred.
 * A 599 will be returned if the recipient is unknown.
 * 
 * Note that authId *or* authName must be present, not both.
 * 
 * TODO:  make this a lot more efficient by using the content body
 * as the raw message bytes instead of requiring www-form-urlencoded 
 */
@SuppressWarnings("serial")
//@Current @ApplicationScoped
public class InjectorServlet extends HttpServlet
{
	/** */
	public static final String AUTH_ID_PARAM = "authId";
	public static final String AUTH_NAME_PARAM = "authName";
	public static final String AUTH_PASS_PARAM = "authPassword";
	public static final String FROM_PARAM = "from";
	public static final String RECIPIENT_PARAM = "recipient";
	public static final String MESSAGE_PARAM = "message";
	
	/**
	 * Status code we return when we don't know what to do with the recipient.
	 */
	public static final int SC_ADDRESS_UNKNOWN = 599;
	
	@Current
	private LoginStatus loginStatus;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String authId = request.getParameter(AUTH_ID_PARAM);
		String authName = request.getParameter(AUTH_NAME_PARAM);
		String authPass = request.getParameter(AUTH_PASS_PARAM);
		String from = request.getParameter(FROM_PARAM);
		String recipient = request.getParameter(RECIPIENT_PARAM);
		String message = request.getParameter(MESSAGE_PARAM);
		
		if (authPass == null || from == null || recipient == null || message == null)
			throw new ServletException("Missing parameter");
		
		if (authId == null && authName == null)
			throw new ServletException("Missing parameter");
		
		if (authId != null && authName != null)
			throw new ServletException("Cannot specify both authId and authName");
		
		// One way or another we need an authId
		if (authName != null)
		{
			try
			{
				AuthCredentials authCred = Backend.instance().getAccountMgr().authenticate(authName, authPass);
				this.loginStatus.SetCreds(authCred);
				authId = authCred.getId().toString();
			}
			catch (FailedLoginException ex) { throw new ServletException(ex); }
		}
		if (!Backend.instance().getInjector().inject(from, recipient, new ByteArrayInputStream(message.getBytes())))
			response.sendError(SC_ADDRESS_UNKNOWN, "Recipient address unknown");
	}
}
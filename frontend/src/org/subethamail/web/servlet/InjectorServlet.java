/*
 * $Id: AuthAction.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/action/AuthAction.java $
 */

package org.subethamail.web.servlet;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.security.SimplePrincipal;
import org.subethamail.core.injector.i.AddressUnknownException;
import org.subethamail.web.Backend;
import org.subethamail.web.security.Security;

/**
 * Servlet allows calling the injector with a very simple HTTP POST
 * rather than going through the SOAP stack.
 * 
 * Parameters expected:
 * 
 * authName: the email address of a site administrator
 * authPassword: the password for the site administrator account
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
	/** */
	public static final String AUTH_NAME_PARAM = "authName";
	public static final String AUTH_PASS_PARAM = "authPassword";
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
		String authName = request.getParameter(AUTH_NAME_PARAM);
		String authPass = request.getParameter(AUTH_PASS_PARAM);
		String recipient = request.getParameter(RECIPIENT_PARAM);
		String message = request.getParameter(MESSAGE_PARAM);
		
		if (authName == null || authPass == null || recipient == null || message == null)
			throw new ServletException("Missing parameter");
		
		try
		{
			Security.associateCredentials(new SimplePrincipal(authName), authPass);
			
			Backend.instance().getInjector().inject(recipient, message.getBytes());
		}
		catch (AddressUnknownException ex)
		{
			response.sendError(SC_ADDRESS_UNKNOWN, "Recipient address unknown");
		}
		catch (MessagingException ex)
		{
			throw new ServletException(ex);
		}
		finally
		{
			Security.disassociateCredentials();
		}
	}
}

/*
 * $Id: ListServlet.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/servlet/ListServlet.java $
 */

package org.subethamail.web.servlet;

import java.io.IOException;

import javax.annotation.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.common.MailUtils;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.Archiver;
import org.subethamail.entity.i.PermissionException;
import org.subethamail.web.Backend;

/**
 * This servlet will return an archived message in its raw rfc2822 format.
 */
@SuppressWarnings("serial")
public class AttachmentServlet extends HttpServlet
{
	@EJB Archiver archiver;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Long attachmentId = null;
		
		String[] pathSplit = request.getPathInfo().split("/");
		
		String tmpId  = (pathSplit.length > 1) ? pathSplit[1] : null;
		//String downloadFilename = (pathSplit.length > 2) ? pathSplit[2] : null;
		
		attachmentId = Long.parseLong(tmpId);
		if (attachmentId == null)
		{
			response.setStatus(500);
			return;
		}
		
		try
		{
			String contentType = Backend.instance().getArchiver().getAttachmentContentType(attachmentId);
			String name = MailUtils.getNameFromContentType(contentType);
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", " attachment; filename=\"" + name + "\"");
			Backend.instance().getArchiver().writeAttachment(attachmentId, response.getOutputStream());
		}
		catch (PermissionException pex)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/error_permission.jsp");
			request.setAttribute("javax.servlet.error.exception", pex);
			dispatcher.forward(request, response);
		}
		catch (NotFoundException nfex)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/error_notfound.jsp");
			request.setAttribute("javax.servlet.error.exception", nfex);
			dispatcher.forward(request, response);
		}
	}
}

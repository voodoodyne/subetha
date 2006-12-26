/*
 * $Id: ListServlet.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/servlet/ListServlet.java $
 */

package org.subethamail.web.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.common.ExportMessagesException;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ExportFormat;
import org.subethamail.entity.i.PermissionException;
import org.subethamail.web.Backend;

/**
 * This servlet will exported messages for a list. The format is passed through.
 * 
 * The format of the url has to be /id/[format]/filename. If format is omitted it will default to rfc2822directory (maildir like).
 */
@SuppressWarnings("serial")
public class ExportServlet extends HttpServlet
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Long listId = null;
		String[] pathSplit = request.getPathInfo().split("/");

		String tmpId = (pathSplit.length > 1) ? pathSplit[1] : null;
		ExportFormat format = null;
		if (pathSplit.length > 2)
		{
			try {
				format = ExportFormat.valueOf(pathSplit[2]);
			}
			catch (IllegalArgumentException iae) {}
			finally {
				format = (format==null) ? ExportFormat.RFC2822DIRECTORY : format; 
			}
		}
		
		listId = Long.parseLong(tmpId);

		try
		{
			if (listId == null) throw new NotFoundException("List not found: invalid id!");
			switch (format)
			{
			case MBOX:
				//response.setHeader("Content-Disposition", "attachment");
				response.setContentType("application/mbox");
				break;

			case RFC2822DIRECTORY:
				response.setHeader("Content-Disposition", "attachment");
				response.setContentType("application/x-compressed");
				break;
			}
			
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			Backend.instance().getArchiver().exportList(listId, format, bos);
		}
		catch (PermissionException pex)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(
					"/error_permission.jsp");
			request.setAttribute("javax.servlet.error.exception", pex);
			dispatcher.forward(request, response);
		}
		catch (NotFoundException nfex)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(
					"/error_notfound.jsp");
			request.setAttribute("javax.servlet.error.exception", nfex);
			dispatcher.forward(request, response);
		}
		catch (ExportMessagesException eme)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/error_notfound.jsp");
			request.setAttribute("javax.servlet.error.exception", eme);
			dispatcher.forward(request, response);
		}
	}
}

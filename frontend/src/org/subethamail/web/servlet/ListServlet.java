/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.common.NotFoundException;
import org.subethamail.web.Backend;

/**
 * This servlet is what allows master list URLs to be
 * human-readable, instead of having a numeric list id.
 * It simply inspects the whole URL, performs a lookup,
 * and then forwards to the normal list.jsp with the
 * new id parameter.
 */
@SuppressWarnings("serial")
public class ListServlet extends HttpServlet
{
	/** */
	public static final String LIST_PAGE = "/list.jsp";
	public static final String ID_PARAM_NAME = "listId";
	
	public static final String BAD_LIST_PAGE = "/list_unknown.jsp";

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String urlString = request.getRequestURL().toString();
		URL url = new URL(urlString);
		
		try
		{
			Long listId = Backend.instance().getListMgr().lookup(url);
			
			RequestDispatcher dispatcher = 
				this.getServletContext().getRequestDispatcher(LIST_PAGE + "?" + ID_PARAM_NAME + "=" + listId);
			
			dispatcher.forward(request, response);
		}
		catch (NotFoundException ex)
		{
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(BAD_LIST_PAGE);
			dispatcher.forward(request, response);
		}
	}
}

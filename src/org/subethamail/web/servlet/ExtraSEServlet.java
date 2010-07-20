/*
 * $Id: ListServlet.java 1353 2010-05-05 23:33:50Z scotthernandez $
 * $URL: https://subetha.googlecode.com/svn/trunk/src/org/subethamail/web/servlet/ListServlet.java $
 */

package org.subethamail.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a somewhat silly servlet.  Some folks run SubEtha on /se/, some run it
 * on the root.  If you run it on the root but someone uses a /se/blah url, this
 * servlet will redirect you to the correct url by stripping out the /se/.  It will
 * help with migrations.
 * 
 * This servlet should be mounted on /se/*
 */
public class ExtraSEServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String uri = request.getRequestURI();
		uri = uri.substring("/se".length());
		
		String query = request.getQueryString();
		if (query != null && query.length() > 0)
			uri = uri + "?" + query;

		response.sendRedirect(uri);
	}
}

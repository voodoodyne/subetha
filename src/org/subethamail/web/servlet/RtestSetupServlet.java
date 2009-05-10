/*
 * $Id: ListServlet.java 378 2006-05-17 00:11:14Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/servlet/ListServlet.java $
 */

package org.subethamail.web.servlet;

import java.io.IOException;

import javax.inject.Current;
import javax.inject.manager.Manager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.subethamail.common.ResinBridge;

/**
 * This servlet is called to setup the channel (resin-bridge) for the rtests
 */
@SuppressWarnings("serial")
public class RtestSetupServlet extends HttpServlet
{
	@Current Manager mgr;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO: Figure out a better way, maybe by using a new 
		// service and listening for the singleton bean to get added to the inject manager?
		ResinBridge rb = (ResinBridge)mgr.getInstanceByName(ResinBridge.NAME);
		
		if (rb != null) rb.setManager(mgr);
		else
			throw new ServletException(new IllegalStateException(
					ResinBridge.NAME + " is not set in the inject manager"));
		
	}
}

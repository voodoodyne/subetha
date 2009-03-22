/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.util.AbstractFilter;


/**
 * Filter which gets credentials from http context and associates them
 * with the current thread using JBoss API.  Credentials are cleared
 * when the request finishes.
 * 
 * @author Jeff Schnitzer
 */
public class SecurityAssociationFilter extends AbstractFilter
{
	/** */
	private static Log log = LogFactory.getLog(SecurityAssociationFilter.class);
	
	/**
	 */
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		if (log.isDebugEnabled())
			log.debug("*** Starting filter for " + request.getRequestURI());

		SecurityContext sctx = null;
		
		try
		{
			HttpSession sess = request.getSession(false);
			if (sess != null)
			{
				sctx = (SecurityContext)sess.getAttribute(SecurityContext.SESSION_KEY);
				if (sctx != null)
					sctx.associateCredentials();
			}
			
			chain.doFilter(request, response);
		}
		finally
		{
			if (sctx != null)
				sctx.disassociateCredentials();
			
			if (log.isDebugEnabled())
				log.debug("*** Ending filter for " + request.getRequestURI());
		}
	}
}

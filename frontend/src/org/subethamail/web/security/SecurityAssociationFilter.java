/*
 * $Id: SecurityAssociationFilter.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/security/SecurityAssociationFilter.java $
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
		
		try
		{
			HttpSession sess = request.getSession(false);
			if (sess != null)
				Security.associateCredentials(sess);
			
			chain.doFilter(request, response);
		}
		finally
		{
			Security.disassociateCredentials();
			
			if (log.isDebugEnabled())
				log.debug("*** Ending filter for " + request.getRequestURI());
		}
	}
}

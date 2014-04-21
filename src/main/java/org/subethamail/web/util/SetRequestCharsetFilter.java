/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.util;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.java.Log;


/**
 * For i18n reasons, we need to set the character encoding on 
 * all requests into the server.
 * 
 * @author Jon Stevens
 */
@Log
public class SetRequestCharsetFilter extends AbstractFilter
{
	/**
	 */
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		if (log.isLoggable(Level.FINE))
			log.log(Level.FINE, "vvv Starting filter for {0}", request.getRequestURI());
		
		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding("UTF-8");
		
		chain.doFilter(request, response);
		
		if (log.isLoggable(Level.FINE))
			log.log(Level.FINE,"^^^ Ending filter for {0}", request.getRequestURI());
	}
}

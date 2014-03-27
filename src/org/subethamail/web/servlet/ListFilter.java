/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import lombok.extern.java.Log;

import org.subethamail.common.NotFoundException;
import org.subethamail.core.lists.i.ListMgr;
import org.subethamail.web.util.AbstractFilter;

/**
 * This is a little trick that lets mailing lists "claim" any URL that successfully
 * resolves to this server.  Mounted on /*, this filter will try to resolve any
 * request normally - but if the result is 404, it will try to lookup the URL as
 * a mailing list URL.  If it finds one, it forwards to the list page.  Finally
 * as a last resort if it can do nothing else it will return 404.
 */
@Log
public class ListFilter extends AbstractFilter
{
	private static final long serialVersionUID = 1L;
	
	/** */
	public static final String LIST_PAGE = "/list.jsp";
	public static final String ID_PARAM_NAME = "listId";
	
	public static final String WELCOME_PAGE = "/welcome.jsp";

	/** Good for shunting output to the bitbucket */
	static final ServletOutputStream NOOP_OUTPUTSTREAM = new ServletOutputStream() {
		@Override public void write(int b) throws IOException {}
		@Override public void write(byte[] b, int off, int len) throws IOException {}
	};
	
	/**
	 * <p>An {@link HttpServletResponseWrapper} that traps HTTP errors by
	 * overriding {@code sendError(int, ..)}.  If the error is 404,
	 * anything written is abandoned.  If the response is anything else,
	 * it is passed through as-is.</p>
	 * 
	 * <p>Note that this wrapper translates getWriter() calls into
	 * getOutputStream() calls to work around broken containers (and
	 * broken specs).
	 */
	public class TrappingResponseWrapper extends HttpServletResponseWrapper
	{
		/** True when we get a 404 */
		boolean trapped;
		
		/** Wrap the given {@code response}. */
		public TrappingResponseWrapper(HttpServletResponse response)
		{
			super(response);
		}

		/** */
		@Override
		public void sendError(int errorCode, String errorMessage) throws IOException
		{
			if (errorCode == HttpServletResponse.SC_NOT_FOUND)
			{
			    log.log(Level.FINE,"Got 404, trapping error");
				this.trapped = true;
			}
			else
				super.sendError(errorCode, errorMessage);
		}

		/** */
		@Override
		public void sendError(int errorCode) throws IOException
		{
			if (errorCode == HttpServletResponse.SC_NOT_FOUND)
			{
			    log.log(Level.FINE,"Got 404, trapping error");
				this.trapped = true;
			}
			else
				super.sendError(errorCode);
		}

		/** */
		@Override
		public ServletOutputStream getOutputStream() throws IOException
		{
		    if (log.isLoggable(Level.FINE))
			    log.log(Level.FINE,"Getting a " + ((this.trapped)?"NOOP":"real")  + " OutputStream");
			
			if (this.trapped)
				return NOOP_OUTPUTSTREAM;
			else
				return super.getOutputStream();
		}

		/**
		 */
		public boolean isTrapped() { return this.trapped; }
	}
	
	/**
	 */
	@Inject ListMgr listMgr;
	
	/* (non-Javadoc)
	 * @see org.subethamail.web.util.AbstractFilter#doFilter(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// We only work for GET requests
		if (!"GET".equals(request.getMethod()))
		{
			chain.doFilter(request, response);
			return;
		}
		
		// First try to process the request normally.  If we get 404, we need
		// to check for a special list address.
		TrappingResponseWrapper responseWrapper = new TrappingResponseWrapper(response);
		chain.doFilter(request, responseWrapper);
		
		// Maybe we're already done
		if (!responseWrapper.isTrapped())
			return;

		// Apparently not, let's lookup the URL and see if we have a hit
		String urlString = request.getRequestURL().toString();
		URL url = new URL(urlString);
		
		try
		{
			Long listId = listMgr.lookup(url);
			
			RequestDispatcher dispatcher = 
				request.getRequestDispatcher(LIST_PAGE + "?" + ID_PARAM_NAME + "=" + listId);
			
			dispatcher.forward(request, response);
		}
		catch (NotFoundException ex)
		{
			RequestDispatcher dispatcher = request.getRequestDispatcher(WELCOME_PAGE);
			dispatcher.forward(request, response);
		}
	}
}

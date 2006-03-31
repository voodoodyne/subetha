/*
 * $Id: AbstractFilter.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/util/AbstractFilter.java $
 */

package org.subethamail.web.servlet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.subethamail.core.acct.i.Receptionist;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.listwiz.i.ListWizard;

/**
 * Singleton which provides access to the backend EJBs.  
 * 
 * This is initialized as a servlet on startup so that it
 * can place itself in application scope; this makes it
 * available to JSPs as ${backend}.
 * 
 * Other classes in the web tier can obtain the instance
 * by calling Backend.instance().
 */
public class Backend extends HttpServlet
{
	/** Application-scope key */
	public static final String KEY = "backend";
	
	/** 
	 * There should only be one instance of this class, but it
	 * is created by the web container.  This static variable is
	 * initialized by the web container on init().
	 */
	static Backend singleton;
	
	/** Stateless ession EJB references are all thread-safe */
	Receptionist receptionist;
	Encryptor encryptor;
	ListWizard listWizard;
	
	/**
	 * Obtain the current instance.
	 */
	public static Backend instance() { return singleton; }
	
	/**
	 * Initialize all the ejb references and make them
	 * available in the application scope.
	 */
	public void init() throws ServletException
	{
		try
		{
			InitialContext ctx = new InitialContext();
			
			receptionist = (Receptionist)ctx.lookup(Receptionist.JNDI_NAME);
			encryptor = (Encryptor)ctx.lookup(Encryptor.JNDI_NAME);
			listWizard = (ListWizard)ctx.lookup(ListWizard.JNDI_NAME);
		}
		catch (NamingException ex) { throw new ServletException(ex); }
		
		this.getServletContext().setAttribute(KEY, this);
		
		singleton = this;
	}

	/** */
	public Encryptor getEncryptor()
	{
		return this.encryptor;
	}

	/** */
	public ListWizard getListWizard()
	{
		return this.listWizard;
	}

	/** */
	public Receptionist getReceptionist()
	{
		return this.receptionist;
	}
	
}

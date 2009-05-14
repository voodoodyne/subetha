/*
 * $Id: Transmute.java 979 2008-10-08 01:14:25Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/Transmute.java $
 */

package org.subethamail.core.util;

import javax.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.core.auth.SubEthaPrincipal;

import com.caucho.security.SecurityContext;
import com.caucho.security.SecurityContextException;



/**
 * This allows you to get the SubEthaPrincipal associated with the current
 * request.  It exists because Resin4 is broken and does not offer a
 * working ejb SessionContext.  SecurityContext can be used to obtain
 * the Principal during normal web requests but this fails during
 * Hessian requests (this is a bug).
 * 
 * The SubEthaAuthenticator is called at the start of a user's session
 * (either a series of web requests or a single hessian call), and
 * it will call setPrincipal() here.  Thus in the case of hessian 
 * (when SecurityContext is broken), we will always have the Principal.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@RequestScoped
public class RequestPrincipalProvider
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(RequestPrincipalProvider.class);

	/** */
	SubEthaPrincipal prince;
	
	/** */
	public RequestPrincipalProvider()
	{
		log.debug("Constructing new RequestPrincipalProvider");
	}
	
	/**
	 * @return the current appropriate Principal, either because one was
	 * set into this object or because we can find it in the container.
	 */
	public SubEthaPrincipal getPrincipal()
	{
		log.debug("getting principal from " + super.toString());
			
		try
		{
			SubEthaPrincipal p = (SubEthaPrincipal)SecurityContext.getUserPrincipal();
			if (log.isDebugEnabled())
				log.debug("Principal obtained from SecurityContext is " + p + ", stored principal is " + this.prince);
			
			if (this.prince != null && p != null && this.prince != p)
				throw new IllegalStateException("Principals did not match");
			
			if (this.prince == null && p == null)
				throw new IllegalStateException("No principal!");
			
			return p != null ? p : this.prince;
		}
		catch (SecurityContextException e) { throw new RuntimeException(e); }
	}
	
	/**
	 * Called by anything that knows the principal accurately.  Tracked through
	 * the request.
	 */
	public void setPrincipal(SubEthaPrincipal principal)
	{
		if (log.isDebugEnabled())
			log.debug("Setting principal to " + principal + " on " + super.toString());
		
		this.prince = principal;
	}
}

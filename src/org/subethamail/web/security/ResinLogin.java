package org.subethamail.web.security;

import java.lang.annotation.Annotation;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.context.ApplicationScoped;
import javax.inject.Current;
import javax.inject.New;
import javax.inject.manager.Manager;
import javax.servlet.http.HttpServletRequest;

import com.caucho.config.inject.SingletonBean;
import com.caucho.config.scope.ScopeContext;
import com.caucho.security.AbstractLogin;
import com.caucho.security.Authenticator;
import com.caucho.security.BasicPrincipal;
import com.caucho.security.Credentials;
import com.caucho.security.MemorySingleSignon;
import com.caucho.security.PasswordCredentials;
import com.caucho.security.SingleSignon;
import com.caucho.server.webbeans.SessionScope;

/**
 * Login class which makes programmatic login available.  You can inject
 * this in your servlet.
 * 
 * @author Jeff Schnitzer
 */
@ApplicationScoped
public class ResinLogin extends AbstractLogin
{
	private static final Logger log = Logger.getLogger(ResinLogin.class.getName());

	@Current Manager mgr;
	@New MemorySingleSignon ss;

	/**
	 * Always use a {@link MemorySingleSignon} just for this module
	 * 
	 */
	@Override
	protected SingleSignon getSingleSignon() {
		return this.ss;
	}

	
	/**
	 * Logs in the user/pass to the container, if the credentials are valid.
	 * 
	 * @return true if success, false if the credentials were bad.
	 */
	public boolean login(String name, String pass, HttpServletRequest request)
	{
		Authenticator auth = this.getAuthenticator();
		
	    BasicPrincipal user = new BasicPrincipal(name);

	    Credentials credentials = new PasswordCredentials(pass);
	    Principal principal = auth.authenticate(user, credentials, request);

	    if (log.isLoggable(Level.FINE))
	      log.fine("extra: " + user + " -> " + principal);
	    
	    if (principal == null)
	    {
	    	return false;
	    }
	    else
	    {
	    	this.saveUser(request, principal);
	    	//try creating a session (singleton) bean for later use
	    	SingletonBean sbean = new SingletonBean(principal, null, null, (Annotation[])null, principal.getClass());
	    	sbean.setScope(new SessionScope());
	    	this.mgr.addBean(sbean);
	    	return true;
	    }
	}

	/**
	 * Logs out the user as far as the container is concerned.
	 */
	public void logout(HttpServletRequest request)
	{
		this.logout(null, request, null);
	}
}
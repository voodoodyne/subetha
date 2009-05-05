package org.subethamail.web.security;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.context.ApplicationScoped;
import javax.inject.New;
import javax.servlet.http.HttpServletRequest;

import com.caucho.security.AbstractLogin;
import com.caucho.security.Authenticator;
import com.caucho.security.BasicPrincipal;
import com.caucho.security.ClusterSingleSignon;
import com.caucho.security.Credentials;
import com.caucho.security.MemorySingleSignon;
import com.caucho.security.PasswordCredentials;
import com.caucho.security.SingleSignon;

/**
 * Login class which makes programmatic login available.  You can inject
 * this in your servlet.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
public class ResinLogin extends AbstractLogin
{
	/** Logger */
	private static final Logger log = Logger.getLogger(ResinLogin.class.getName());

	/** Need to use this because of problems with the {@link ClusterSingleSignon} one **/
	@New MemorySingleSignon ss;

	/**
	 * Always use a {@link MemorySingleSignon} just for this module
	 * 
	 */
	@Override
	protected SingleSignon getSingleSignon() {
		return this.ss;
	}

	@PostConstruct
	protected void postConstruct() {
		//just here for debugging.
	    if (log.isLoggable(Level.FINE))
		      log.fine("ResinLogin->postConstruct; ss=" + this.ss );
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
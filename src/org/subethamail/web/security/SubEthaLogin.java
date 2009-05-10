package org.subethamail.web.security;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import com.caucho.security.AbstractLogin;
import com.caucho.security.Authenticator;
import com.caucho.security.BasicPrincipal;
import com.caucho.security.ClusterSingleSignon;
import com.caucho.security.Credentials;
import com.caucho.security.MemorySingleSignon;
import com.caucho.security.PasswordCredentials;

/**
 * Login class which makes programmatic login available.  You can inject
 * this in your servlet.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@ApplicationScoped
public class SubEthaLogin extends AbstractLogin
{
	/** Logger */
	private static final Logger log = Logger.getLogger(SubEthaLogin.class.getName());

	/** Need to use this because of problems with the default {@link ClusterSingleSignon} **/
	public SubEthaLogin(){
		super();
		
		MemorySingleSignon mss =  new MemorySingleSignon(); //do it manually. :(
		mss.init(); // call init since we aren't allowing the injection system to create the object.
		this._singleSignon = mss;
	}
	
	/**
	 * 
	 * Logs in the user/pass to the container, if the credentials are valid.
	 * 
	 * @param email the email address
	 * @param pass	the cleartext password
	 * 
	 * @return true if success, false if the credentials were bad.
	 */
	public boolean login(String email, String pass, HttpServletRequest request)
	{
		Authenticator auth = this.getAuthenticator();
		
		/** send a null id, it will be fixed over there */
	    BasicPrincipal user = new BasicPrincipal(email);
	    Credentials credentials = new PasswordCredentials(pass);
	    
	    Principal principal = auth.authenticate(user, credentials, null);

	    if (log.isLoggable(Level.FINE))
	      log.fine("authenticated: " + user + " -> " + principal);
	    
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
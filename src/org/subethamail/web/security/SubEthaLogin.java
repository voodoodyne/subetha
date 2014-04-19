package org.subethamail.web.security;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.java.Log;

import com.caucho.security.Authenticator;
import com.caucho.security.BasicLogin;
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
@Log
@ApplicationScoped
public class SubEthaLogin extends BasicLogin
{
	/** Need to use this because of problems with the default {@link ClusterSingleSignon} **/
	public SubEthaLogin()
	{
		MemorySingleSignon mss =  new MemorySingleSignon(); // do it manually. :(
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

	    log.log(Level.FINE,"authenticated: {0} -> {1}", new Object[]{user, principal});
	    
	    if (principal == null)
	    {
	    	return false;
	    }
	    else
	    {
	        log.log(Level.FINE,"saving user with request: {0}",request);
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
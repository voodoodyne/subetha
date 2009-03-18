/*
 * $Id: $
 * $URL: $
 */

package org.subethamail.core.acct.i;

import java.io.Serializable;

import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.inject.Initializer;

/**
 * Stores information about the LoginStatus
 * 
 * This object is SessionScoped and one will always exist, therefore.
 * If there associated AuthCredentials then the user has authenticated.
 * 
 * @author Scott Hernandez
 */
@SessionScoped @Named
public class LoginStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	AuthCredentials creds = null;
	
	@Initializer
	public LoginStatus(){}
	
	public boolean isLoggedIn() 
	{ 
		return (null != this.creds);
	}
	
	/**
	 * Returns the current AuthCredentials for this LoginStatus
	 * @return
	 */
	public AuthCredentials GetCreds(){
		return creds;	
	}
	/**
	 * Sets the AuthCredential for this LoginStatus
	 * @param value The AuthCredentials to associate
	 * @return The AuthCredentials set
	 */
	public AuthCredentials SetCreds(AuthCredentials value){
		this.creds = value;
		return this.creds;
	}
	/**
	 * Clears the current LoginStatus AuthCredentials
	 */
	public void ClearCreds(){ this.creds = null;}
}

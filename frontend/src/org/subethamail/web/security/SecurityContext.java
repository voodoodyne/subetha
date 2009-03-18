/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.security;

import java.security.Principal;
import java.util.Set;

import javax.context.SessionScoped;
import javax.inject.Current;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;


/**
 * Hides the mess of security management in the web tier.  The
 * mess is caused by two things:
 * 
 * 1) J2EE security APIs are deficient to the point of defective.
 * 2) JBoss' security APIs are convoluted as all hell.
 * 
 * Putting one of these in a user's HTTP session and
 * enabling the SecurityAssociationFilter will give
 * you most of the way you wished the web tier worked.
 */
public class SecurityContext
{
	/** */
	private Log log;
	
	/** Key in the session attrs that this object should be stored */
	public static final String SESSION_KEY = "subetha.security.context";

	/** */
	Principal principal;
	String credential;
	Set<String> roles;

	/**
	 * Logs in the user, as far as jboss security is concerned.
	 * Assumes that the credentials and the roles are valid.
	 */
	public SecurityContext(String name, String password, Set<String> roles)
	{
		this.principal = new SimplePrincipal(name);
		this.credential = password;
		this.roles = roles;
	}
	
	/**
	 * Changes just the associated password.
	 */
	public void setPassword(String password)
	{
		log.debug("Changing password associated with session");
		
		this.credential = password;
		
		this.associateCredentials();
	}
	
	/**
	 * @return the "correct" value for ServletRequest.getUserPrincipal()
	 */
	public Principal getUserPrincipal()
	{
		return this.principal;
	}
	
	/**
	 * Checks the jboss security manager and caches the result.
	 * 
	 * @return the "correct" value for ServletRequest.isUserInRole()
	 */
	public boolean isUserInRole(String role)
	{
		return this.roles.contains(role);
	}
	
	/** 
	 * If user is logged in, associate the principal with the jboss
	 * security context.  To be called by a filter.
	 */
	public void associateCredentials()
	{
		if (log.isDebugEnabled())
			log.debug("Associating credentials for " + this.principal);
		
		SecurityAssociation.setPrincipal(this.principal);
		SecurityAssociation.setCredential(this.credential);
	}
	
	/**
	 * Removes everything associated with the security context.
	 * To be called by a filter. 
	 */
	public void disassociateCredentials()
	{
		log.debug("Disassociating all credentials");
		
		SecurityAssociation.setPrincipal(null);
		SecurityAssociation.setCredential(null);
	}
}

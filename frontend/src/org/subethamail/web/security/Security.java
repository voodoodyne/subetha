/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.security;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.AuthenticationManager;
import org.jboss.security.RealmMapping;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;


/**
 * Static methods to hide the hacking of JBoss security that we have
 * to do in order to make J2EE security work from the web component.
 * 
 * Calling these methods has the effect of setting auth as JBoss
 * sees it.  Works in concert with the SecurityAssociationFilter.
 */
public class Security
{
	/** */
	private static Log log = LogFactory.getLog(Security.class);
	
	/** JAAS security context in JNDI */
	public final static String JAAS_CONTEXT = "java:/jaas/subetha";

	/** Key in session attrs for manually maintained principal name */
	public final static String PRINCIPAL_KEY = "subetha.j2ee.principal";

	/** Key in session attrs for manually maintained credential */
	public final static String CREDENTIAL_KEY = "subetha.j2ee.credential";

	/** The j2ee security role for authenticated users */
	public final static String USER_ROLE = "user";
	
	/** Prefix for roles cached in the http session */
	protected final static String ROLE_CACHE_PREFIX = "subetha.role.";
	
	
	/**
	 * Logs out the user, as far as jboss security is concerned.
	 */
	public static void logout(HttpSession sess)
	{
		log.debug("Logging out");
		
		sess.removeAttribute(PRINCIPAL_KEY);
		sess.removeAttribute(CREDENTIAL_KEY);
		
		disassociateCredentials();
	}
	
	/**
	 * Logs in the user, as far as jboss security is concerned.
	 * Assumes that the credentials are valid.
	 */
	public static void login(HttpSession sess, String name, String password)
	{
		if (log.isDebugEnabled())
			log.debug("Logging in " + name);
		
		Principal prin = new SimplePrincipal(name);
		
		sess.setAttribute(PRINCIPAL_KEY, prin);
		sess.setAttribute(CREDENTIAL_KEY, password);
		
		associateCredentials(sess);
	}
	
	/**
	 * Changes just the associated password.
	 */
	public static void setPassword(HttpSession sess, String password)
	{
		log.debug("Changing password associated with session");
		
		sess.setAttribute(CREDENTIAL_KEY, password);
		
		associateCredentials(sess);
	}
	
	/**
	 * @return whether or not the user is currently logged in
	 */
	public static boolean isLoggedIn(HttpServletRequest request)
	{
		return isUserInRole(USER_ROLE, request);
	}
	
	/**
	 * This checks the jboss security manager and caches the result in the
	 * http session.
	 */
	public static boolean isUserInRole(String role, HttpServletRequest request)
	{
		Principal user = getUserPrincipal(request);
		if (user == null)
			return false;
		
		Object credential = request.getSession().getAttribute(CREDENTIAL_KEY);
		
		String cacheKey = ROLE_CACHE_PREFIX + role;

		Boolean cached = (Boolean)request.getSession().getAttribute(cacheKey);
		if (cached != null)
			return cached.booleanValue();
		
		// Ok, we're going to have to figure it out on our own
		try
		{
			Context ctx = new InitialContext();
			RealmMapping mapping = (RealmMapping)ctx.lookup(JAAS_CONTEXT);
			AuthenticationManager authMan = (AuthenticationManager)mapping;
			
			// This bit copied from JaasSecurityManagerService.doesUserHaveRole()
			
			// Validate the credentials, populate the subject
			Subject subject = new Subject();
			authMan.isValid(user, credential, subject);
			
			Set roleSet = Collections.singleton(new SimplePrincipal(role));
			
			SecurityAssociation.pushSubjectContext(subject, user, credential);
			boolean result = mapping.doesUserHaveRole(user, roleSet);
			SecurityAssociation.popSubjectContext();
			
			if (log.isDebugEnabled())
				log.debug("Is " + user + " in role " + role + "?  " + result);
			
			// Save it in the cache
			request.getSession().setAttribute(cacheKey, new Boolean(result));
				
			return result;
		}
		catch (NamingException ex)
		{
			throw new IllegalStateException("Bad configuration of security?", ex);
		}
	}
	
	/**
	 * @return the "correct" value for ServletRequest.getUserPrincipal()
	 */
	public static Principal getUserPrincipal(HttpServletRequest request)
	{
		// This is what it should be:
		// return request.getUserPrincipal();
		
		return (Principal)request.getSession().getAttribute(PRINCIPAL_KEY);
	}
	
	/** 
	 * If user is logged in, associate the principal with the jboss
	 * security context.  To be called by a filter.
	 */
	public static void associateCredentials(HttpSession sess)
	{
		Principal prin = (Principal)sess.getAttribute(PRINCIPAL_KEY);
		if (prin != null)
		{
			if (log.isDebugEnabled())
				log.debug("Associating credentials for " + prin);
			
			Object cred = sess.getAttribute(CREDENTIAL_KEY);
			
			associateCredentials(prin, cred);
		}
	}
	
	/** 
	 * If user is logged in, associate the principal with the jboss
	 * security context.  To be called by a filter.
	 */
	public static void associateCredentials(Principal prin, Object credential)
	{
		if (log.isDebugEnabled())
			log.debug("Associating credentials for " + prin);
		
		SecurityAssociation.setPrincipal(prin);
		SecurityAssociation.setCredential(credential);
	}
	
	/**
	 * Removes everything associated with the security context.
	 * To be called by a filter. 
	 */
	public static void disassociateCredentials()
	{
		log.debug("Disassociating all credentials");
		
		SecurityAssociation.setPrincipal(null);
		SecurityAssociation.setCredential(null);
	}
}

package org.subethamail.core.util;

import javax.annotation.Resource;
import javax.context.ApplicationScoped;
import javax.inject.Current;
import javax.inject.Produces;
import javax.inject.manager.InjectionPoint;
import javax.mail.Session;

import org.subethamail.web.security.ResinLogin;

/**
 * Producers used for creating things with context. Yeah!
 * 
 * @author Scott Hernandez
 *
 */
@ApplicationScoped
public class Producers {

//	@PersistenceContext
//	EntityManager em;
//	
	@Resource(name="java:comp/env/mail")
	Session ses;
	
	@Current
	ResinLogin login;
	
//	@Produces
//	SubEthaEntityManager createSubEthaEntityManager(InjectionPoint ip){
//		return new SubEthaEntityManager(this.em);	
//	}

	@Produces
	Session	createMailSession(InjectionPoint ip){
		return this.ses;
	}
	
	/*
	@Produces
	Principal getCurrentPrince(InjectionPoint ip)
	{
		return login.getUserPrincipal(null);		
	}
	*/
}
/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/SimilarityLoginModule.java,v $
 */

package org.subethamail.core.util;

import java.security.acl.Group;
import java.util.Arrays;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;
import org.subethamail.common.NotFoundException;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.dao.DAO;

/**
 * Authenticates against the database
 *
 * @author Jeff Schnitzer
 */
public class SubEthaLoginModule extends UsernamePasswordLoginModule
{
	/**
	 */
	private static Log log = LogFactory.getLog(SubEthaLoginModule.class);
	
	/**
	 * We need this to lookup data objects
	 */
	protected static DAO dao;
	
	protected static DAO getDAO()
	{
		if (dao == null)
		{
			try
			{
				Context ctx = new InitialContext();
				dao = (DAO)ctx.lookup(DAO.JNDI_NAME);
			}
			catch (NamingException ex) { throw new RuntimeException(ex); }
		}
		
		return dao; 
	} 
	
	/** */
	protected static final Group[] EMPTY_ROLES = { new SimpleGroup("Roles") };
	
	/** */
	protected static final Group[] USER_ROLES = { new SimpleGroup("Roles") };
	static
	{
		USER_ROLES[0].addMember(new SimplePrincipal("user"));
	}
	
	/** */
	protected static final Group[] SITE_ADMIN_ROLES = { new SimpleGroup("Roles") };
	static
	{
		SITE_ADMIN_ROLES[0].addMember(new SimplePrincipal("user"));
		SITE_ADMIN_ROLES[0].addMember(new SimplePrincipal("siteAdmin"));
	}

	
	/**
	 * Processed from Admin.getRoles()
	 */
	protected Group[] roles;
	
	/**
	 */
	@Override
	protected Group[] getRoleSets() throws LoginException
	{
		if (this.getIdentity().equals(this.unauthenticatedIdentity))
		{
			log.debug("getRoleSets() returning EMPTY_ROLES");
			return EMPTY_ROLES;
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("getRoleSets() returning " + Arrays.toString(this.roles));
			
			return this.roles;
		}
	}

	@Override
	protected String getUsersPassword() throws LoginException
	{
		String email = this.getUsername();
		
		if (log.isDebugEnabled())
			log.debug("Checking password for " + email);

		try
		{
			EmailAddress addy = getDAO().findEmailAddress(email);
			
			if (addy.getPerson().isSiteAdmin())
				this.roles = SITE_ADMIN_ROLES;
			else
				this.roles = USER_ROLES;
			
			return addy.getPerson().getPassword();
		}
		catch (NotFoundException ex)
		{
			throw new FailedLoginException("No such user");
		}
	}
}

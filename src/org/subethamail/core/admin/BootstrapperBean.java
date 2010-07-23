/*
 * $Id: BootstrapperBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/BootstrapperBean.java $
 */

package org.subethamail.core.admin;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Config;

/**
 * This bean really is only used when run for the first time
 * after a fresh install.  It makes sure there is one account
 * with siteAdmin permissions.
 * 
 * For the moment, the email address and password are hard
 * coded to "root@localhost" and "password".
 * 
 * TODO:  These default values should probably be dynamically
 * obtained, perhaps from a system property?
 * 
 * Note that this bean has neither remote nor local interfaces.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Startup
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BootstrapperBean
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(BootstrapperBean.class);
	
	/**
	 */
	private static final String DEFAULT_EMAIL = "root@localhost";
	private static final String DEFAULT_NAME = "Administrator";
	private static final String DEFAULT_PASSWORD = "password";
	
	private static final Integer VERSION_ID = 1;
	
	/**
	 * The config id of a Boolean that lets us know if we've run or not.
	 */
	public static final String BOOTSTRAPPED_CONFIG_ID = "bootstrapped";
	
	/** */
	@Inject Admin admin;

	/** */
	@Inject @SubEtha SubEthaEntityManager em;
	
	/* */
	@PostConstruct
	public void start() throws Exception
	{
		// If we haven't been bootstrapped, we need to run.
		try
		{
			Config cfg = this.em.get(Config.class, BOOTSTRAPPED_CONFIG_ID);
			
			// Might as well sanity check it
			Integer value = (Integer)cfg.getValue();
			
			if (value == null)
			{
				this.bootstrap();
				cfg.setValue(VERSION_ID);
			}
		}
		catch (NotFoundException ex)
		{
			this.bootstrap();
			
			Config cfg = new Config(BOOTSTRAPPED_CONFIG_ID, VERSION_ID);
			this.em.persist(cfg);
		}
	}
	
	/**
	 * Creates the appropriate username and password
	 */
	public void bootstrap()
	{
		log.debug("Bootstrapping - establishing default site administrator");
		this.bootstrapRoot();
	}
	
	/**
	 * Sets up the root account
	 */
	protected void bootstrapRoot()
	{
		InternetAddress addy;
		try
		{
			addy = new InternetAddress(DEFAULT_EMAIL, DEFAULT_NAME);
		}
		catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
		
		Long id = null;
		try
		{
			id = this.admin.establishPerson(addy, DEFAULT_PASSWORD);
		}
		catch (RuntimeException ex)
		{
			log.error("What is up with this error?", ex);
			throw ex;
		}
		
		try
		{
			this.admin.setSiteAdmin(id, true);
		}
		catch (NotFoundException ex)
		{
			log.error("Impossible to establish person and then not find!", ex);
			throw new RuntimeException(ex);
		}
	}
}

/*
 * $Id: BootstrapperBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/BootstrapperBean.java $
 */

package org.subethamail.core.admin;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.SiteUtils;
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

// TODO: Add @Service, (remove scope?) back once bug is fixed in Resin. For now this is triggered by 
// the Backend Servlet (started with web container): http://bugs.caucho.com/view.php?id=3429

//@Service
@ApplicationScoped
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
	
	private static final InternetAddress DEFAULT_SITE_POSTMASTER;
	static
	{
		try
		{
			DEFAULT_SITE_POSTMASTER = new InternetAddress("postmaster@needsconfiguration", "Needs Configuration");
		}
		catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
	}
	
	private static final Integer VERSION_ID = 1;
	
	/**
	 * The config id of a Boolean that lets us know if we've run or not.
	 */
	public static final String BOOTSTRAPPED_CONFIG_ID = "bootstrapped";
	
	/** */
	@Inject
	SiteUtils siteUtils;
	
	/** */
	@Inject Admin admin;

	/** */
	@SubEtha
	protected SubEthaEntityManager em;

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
		
		log.debug("Bootstrapping - establishing default site settings");
		this.bootstrapSiteSettings();
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
		
		Long id = this.admin.establishPerson(addy, DEFAULT_PASSWORD);
		
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
	
	/**
	 * Sets up the initial site settings
	 */
	protected void bootstrapSiteSettings()
	{
		try
		{
			Config cfg = this.em.get(Config.class, Config.ID_SITE_POSTMASTER);
			if (cfg.getValue() == null)
				cfg.setValue(DEFAULT_SITE_POSTMASTER);
		}
		catch (NotFoundException ex)
		{
			Config cfg = new Config(Config.ID_SITE_POSTMASTER, DEFAULT_SITE_POSTMASTER);
			this.em.persist(cfg);
		}
		
		URL defaultSiteUrl;
		try
		{
			defaultSiteUrl = new URL("http://needsconfiguration/" + siteUtils.getContextPath());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		try
		{
			Config cfg = this.em.get(Config.class, Config.ID_SITE_URL);
			if (cfg.getValue() == null)
				cfg.setValue(defaultSiteUrl);
		}
		catch (NotFoundException ex)
		{
			Config cfg = new Config(Config.ID_SITE_URL, defaultSiteUrl);
			this.em.persist(cfg);
		}
	}
}

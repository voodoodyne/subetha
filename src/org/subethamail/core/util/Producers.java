package org.subethamail.core.util;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.mail.Session;
import javax.sql.DataSource;

import org.subethamail.core.post.OutboundMTA;

/**
 * Producers used for creating things with context. Yeah!
 * 
 * @author Scott Hernandez
 * @author Jeff Schnitzer
 */
@Singleton
@Startup
public class Producers
{
	/** 
	 * We need this so that objects which are not CDI-friendly (ie MessageContentBridge)
	 * can get access to injected things.
	 */
	public static Producers instance() { return instance; }
	private static Producers instance;
	
	/** Our application's data source */
	@Inject @Named("jdbc/subetha")
	private DataSource ds;

	@Produces @SubEtha
	public DataSource getSubethaDS()
	{
		return this.ds;
	}

	/** The JavaMail session that connects to the outbound mta */
	@Inject @Named("outbound")
	private Session mailSession;
	
	@Produces @OutboundMTA
	public Session getMailSession()
	{
		return this.mailSession;
	}
	
	/** Set up the static instance */
	@PostConstruct
	public void setup()
	{
		instance = this;
	}
}
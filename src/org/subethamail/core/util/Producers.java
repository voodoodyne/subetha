package org.subethamail.core.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * Producers used for creating things with context. Yeah!
 * 
 * @author Scott Hernandez
 * @author Jeff Schnitzer
 */
@ApplicationScoped
public class Producers
{
	/** Our application's data source */
	@Inject
	@Named("jdbc/subetha")
	private DataSource ds;

	@Produces @SubEtha
	public DataSource getSubethaDS()
	{
		return this.ds;
	}
//
//	/** The JavaMail session that connects to the outbound mta */
//	@Inject @Named("outbound")
//	private Session mailSession;
//	
//	@Produces @OutboundMTA
//	public Session getMailSession()
//	{
//		return this.mailSession;
//	}
}
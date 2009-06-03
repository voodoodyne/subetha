package org.subethamail.core.util;

import javax.context.ApplicationScoped;
import javax.inject.Produces;
import javax.mail.Session;
import javax.sql.DataSource;

import org.subethamail.core.post.OutboundMTA;

import com.caucho.config.Name;

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
	@Name("jdbc/subetha")
	private DataSource ds;

	@Produces @SubEtha
	public DataSource getSubethaDS()
	{
		return this.ds;
	}

	/** The JavaMail session that connects to the outbound mta */
	@Name("outbound")
	private Session mailSession;
	
	@Produces @OutboundMTA
	public Session getMailSession()
	{
		return this.mailSession;
	}
}
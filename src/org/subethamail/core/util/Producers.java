package org.subethamail.core.util;

import javax.annotation.Resource;
import javax.context.ApplicationScoped;
import javax.inject.Produces;
import javax.mail.Session;
import javax.sql.DataSource;

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
	//@Resource(name="java:comp/env/jdbc/subetha")
	@Name("jdbc/subetha")
	private DataSource ds;

	@Produces @Name("subetha")
	public DataSource getSubethaDS()
	{
		return this.ds;
	}

	/** The JavaMail session that connects to the outbound mta */
	@Resource(name="java:comp/env/outbound-mail")
	private Session mailSession;
	
	@Produces @Name("mta")
	public Session getMailSession()
	{
		return this.mailSession;
	}
}
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
 *
 */
@ApplicationScoped
public class Producers {

	@Resource(name="java:comp/env/jdbc/subetha")
	private DataSource ds;

	@Resource(name="java:comp/env/outbound-mail")
	private Session ses;
	
//	@Current
//	Manager mgr;

//	@Produces //@Name("subetha")
//	public Session createMailSession(){
//		return this.ses;
//	}
	
	@Produces @Name("subetha")
	public DataSource createSubethaDS(){
		return this.ds;
	}
	
	//Didn't work.
//	@Produces
//	public InjectBeanHelper<Filter> createFilterIBH(){
//		return new InjectBeanHelper<Filter>(this.mgr);
//	}
//	
//	@Produces
//	public InjectBeanHelper<Blueprint> createBlueprintIBH(){
//		return new InjectBeanHelper<Blueprint>(this.mgr);
//	}
}
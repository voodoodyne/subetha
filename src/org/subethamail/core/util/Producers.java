package org.subethamail.core.util;

import javax.annotation.Resource;
import javax.context.SessionScoped;
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
@SessionScoped
public class Producers {

	//@Resource(name="java:comp/env/jdbc/subetha")
	@Name("jdbc/subetha")
	private DataSource ds;

	@SuppressWarnings("unused")
	@Resource(name="java:comp/env/outbound-mail")
	private Session ses;
	
//	@Resource(name="java:comp/EntityManager") 
//	private EntityManager em;

	@Produces @Name("subetha")
	public DataSource createSubethaDS(){
		return this.ds;
	}

	
//	@Produces @Name("subetha")
//	public SubEthaEntityManager createEntityManager(){
//		return new SubEthaEntityManager(this.em);
//	}

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
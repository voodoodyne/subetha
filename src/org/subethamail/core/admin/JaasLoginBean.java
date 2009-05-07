/*
 * $Id: JaasLoginBean.java 735 2006-08-20 04:21:14Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/JaasLoginBean.java $
 */

package org.subethamail.core.admin;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Person;

/**
 * Implementation of the JaasLogin interface.  Important that
 * this doesn't have any security annotation otherwise
 * we'll get stack overflows when this method gets called from
 * the login module.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="JaasLogin")
public class JaasLoginBean extends EntityManipulatorBean implements JaasLogin
{
	/** */
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(JaasLoginBean.class);

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.JaasLogin#getPerson(java.lang.Long)
	 */
	public Person getPerson(Long id) throws NotFoundException
	{
		return this.em.get(Person.class, id);
	}
	
}

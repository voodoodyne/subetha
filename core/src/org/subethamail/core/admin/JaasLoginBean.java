/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.EmailAddress;
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
	private static Log log = LogFactory.getLog(JaasLoginBean.class);

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.JaasLogin#getPersonForEmail(java.lang.String)
	 */
	public Person getPersonForEmail(String email) throws NotFoundException
	{
		return this.em.get(EmailAddress.class, email).getPerson();
	}
	
}

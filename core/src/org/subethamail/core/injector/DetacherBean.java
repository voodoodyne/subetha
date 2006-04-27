/*
 * $Id: InjectorBean.java 203 2006-04-26 08:24:20Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/core/src/org/subethamail/core/injector/InjectorBean.java $
 */

package org.subethamail.core.injector;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.entity.Mail;

/**
 * @author Jeff Schnitzer
 */
@Stateless(name="Detacher")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
public class DetacherBean implements Detacher
{
	/** */
	private static Log log = LogFactory.getLog(DetacherBean.class);

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Detacher#detach(javax.mail.internet.MimeMessage, org.subethamail.entity.Mail)
	 */
	public void detach(MimeMessage msg, Mail ownerMail) throws MessagingException
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.injector.i.Detacher#attach(javax.mail.internet.MimeMessage)
	 */
	public void attach(MimeMessage msg) throws MessagingException
	{
		// TODO Auto-generated method stub
		
	}
	
}

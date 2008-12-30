/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin;

import java.util.Iterator;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.RemoteBinding;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.jboss.mail.MailServiceMBean;
import org.jboss.mx.util.MBeanProxyExt;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.subethamail.core.admin.i.Plumber;
import org.subethamail.core.admin.i.PlumberManagement;
import org.subethamail.core.admin.i.PlumberRemote;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.PersonalBean;

/**
 * @author Jeff Schnitzer
 */
@Service(name="Plumber", objectName="subetha:service=Plumber")
@SecurityDomain("subetha")
@PermitAll
@LocalBinding(jndiBinding=Plumber.JNDI_NAME)
@RemoteBinding(jndiBinding=PlumberRemote.JNDI_NAME)
public class PlumberBean extends PersonalBean implements Plumber, PlumberRemote, PlumberManagement
{
	/** */
	private static Log log = LogFactory.getLog(PlumberBean.class);

	@EJB PostOffice postOffice;

	// Holds the mail config when overriding
	org.w3c.dom.Element mailConfig;

	/* (non-Javadoc)
	 * @see com.kink.heart.biz.admin.i.Plumber#log(java.lang.String)
	 */
	public void log(String msg)
	{
		log.info(msg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.kink.heart.biz.system.i.Plumber#overrideSmtpServer(java.lang.String)
	 */
	@RolesAllowed("siteAdmin")
	public void overrideSmtpServer(String host)
	{
		if (this.mailConfig != null)
			throw new IllegalStateException("Smtp server override already in effect");

		// If there was a port, separate the two
		String port = null;

		int colon = host.indexOf(':');
		if (colon > 0)
		{
			port = host.substring(colon + 1);
			host = host.substring(0, colon);
		}

		MailServiceMBean mailService = this.getMailService();

		this.mailConfig = mailService.getConfiguration();

		// Note we are now using JDOM elements
		DOMBuilder builder = new DOMBuilder();
		Element config = builder.build(this.mailConfig);

		// Remove any existing host or port
		Iterator<?> it = config.getChildren("property").iterator();
		while (it.hasNext())
		{
			Element prop = (Element)it.next();
			String name = prop.getAttributeValue("name");
			if (name.equals("mail.smtp.host") || name.equals("mail.smtp.port"))
				it.remove();
		}

		// Add host (and maybe port) back with override
		Element hostProp = new Element("property");
		hostProp.setAttribute("name", "mail.smtp.host");
		hostProp.setAttribute("value", host);
		config.addContent(hostProp);

		if (port != null)
		{
			Element portProp = new Element("property");
			portProp.setAttribute("name", "mail.smtp.port");
			portProp.setAttribute("value", port);
			config.addContent(portProp);
		}

		if (log.isInfoEnabled())
		{
			XMLOutputter out = new XMLOutputter();
			log.info("Updated mail config is:  " + out.outputString(config));
		}

		try
		{
			DOMOutputter outputter = new DOMOutputter();
			Document doc = config.getDocument();

			mailService.stop();
			mailService.setConfiguration(outputter.output(doc).getDocumentElement());
			mailService.start();
		}
		catch (Exception ex) { throw new EJBException(ex); }
	}

	/*
	 * (non-Javadoc)
	 * @see com.kink.heart.biz.system.i.Plumber#restoreStmpServer()
	 */
	@RolesAllowed("siteAdmin")
	public void restoreStmpServer()
	{
		if (this.mailConfig == null)
		{
			log.warn("No override in effect; ignoring restoreSmtpServer()");
		}
		else
		{
			log.info("Restoring base mail configuration");
			MailServiceMBean mailService = this.getMailService();
			mailService.stop();
			mailService.setConfiguration(this.mailConfig);
			try
			{
				mailService.start();
			}
			catch (Exception ex) { throw new EJBException(ex); }

			this.mailConfig = null;
		}
	}

	/** @return the JMX mail service */
	protected MailServiceMBean getMailService()
	{
		try
		{
			// Find the mail service in JMX and create a proxy to it
			ObjectName mailServiceName = new ObjectName("jboss:service=Mail");

			// Create Proxy-Object for this service
			return (MailServiceMBean)MBeanProxyExt.create(MailServiceMBean.class, mailServiceName);
		}
		catch (MalformedObjectNameException ex) { throw new EJBException(ex); }
	}
}

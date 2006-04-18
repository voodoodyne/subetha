/*
 * $Id: AccountMgrRemote.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgrRemote.java $
 */

package org.subethamail.smtp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.smtp.i.SMTPListener;
import org.subethamail.smtp.i.SMTPListenerRegistry;

/**
 * @author Jeff Schnitzer
 */
@Service(name="SMTPService", objectName="subetha:service=SMTP")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class SMTPService implements SMTPListenerRegistry, SMTPManagement
{
	/** */
	private static Log log = LogFactory.getLog(SMTPService.class);

	/**
	 * There is no ConcurrentHashSet, so we make up our own by mapping the
	 * object to itself.
	 */
	Map<SMTPListener, SMTPListener> listeners = new ConcurrentHashMap<SMTPListener, SMTPListener>();

	/**
	 * @see SMTPListenerRegistry#register(SMTPListener)
	 */
	public void register(SMTPListener listener)
	{
		if (log.isInfoEnabled())
			log.info("Registering " + listener);
			
		this.listeners.put(listener, listener);
	}

	/**
	 * @see SMTPListenerRegistry#deregister(SMTPListener)
	 */
	public void deregister(SMTPListener listener)
	{
		if (log.isInfoEnabled())
			log.info("De-registering " + listener);
			
		this.listeners.remove(listener);
	}

	/**
	 * @see SMTPManagement#start()
	 */
	@PermitAll
	public void start() throws Exception
	{
		log.info("Starting SMTP service");
		// TODO Auto-generated method stub
	}

	/**
	 * @see SMTPManagement#stop()
	 */
	@PermitAll
	public void stop()
	{
		log.info("Stopping SMTP service");
		// TODO Auto-generated method stub
	}

}

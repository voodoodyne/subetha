/*
 * $Id$
 * $URL$
 */
package org.subethamail.core.postfix;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Depends;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.smtp.SMTPManagement;

/**
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=TcpTable")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class TcpTableService implements TcpTableManagement
{
	@EJB Injector injector;
	@Depends("subetha:service=SMTP") SMTPManagement smtpManagement;

	/** */
	private static Log log = LogFactory.getLog(TcpTableService.class);
	
	/** */
	public static final int DEFAULT_PORT = 2502;	
	private int port = DEFAULT_PORT;

	private String hostName = null;
	
	private TcpTableServer server;
	
	@PermitAll
	public void start() throws IOException
	{
		if (this.server != null)
			throw new IllegalStateException("TcpTableServer already running");
		
		InetAddress binding = null;
		
		String bindAddress = System.getProperty("jboss.bind.address");
		if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
			binding = InetAddress.getByName(bindAddress);

		log.info("Starting TcpTable service: " + (binding==null ? "*" : binding) + ":" + port);
		
		this.server = new TcpTableServer(this);
		this.server.setBindAddress(binding);
		this.server.setPort(this.port);
		
		if (this.hostName != null)
			this.server.setHostName(this.hostName);
		
		this.server.start();
	}

	@PermitAll
	public void stop()
	{
		log.info("Stopping TcpTable service");
		this.server.stop();
		this.server = null;
	}

	@PermitAll
	public void setPort(int port)
	{
		if (this.server != null)
			throw new IllegalStateException("TcpTable already running");
		
		this.port = port;
	}

	@PermitAll
	public int getPort()
	{
		return this.port;
	}

	@PermitAll
	public void setHostName(String hostname)
	{
		if (this.server != null)
			throw new IllegalStateException("TcpTable already running");
		
		this.hostName = hostname;
	}

	@PermitAll
	public String getHostName()
	{
		return this.hostName;
	}
}

/*
 * $Id$
 * $URL$
 */
package org.subethamail.core.postfix;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.jboss.annotation.ejb.Depends;
import org.jboss.annotation.ejb.Service;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.smtp.SMTPManagement;

/**
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
@Service(objectName="subetha:service=TcpTable")
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
	
	private SocketAcceptor acceptor;
	
	/** */
	public class Handler extends IoHandlerAdapter
	{
		/* (non-Javadoc)
		 * @see org.apache.mina.common.IoHandlerAdapter#sessionOpened(org.apache.mina.common.IoSession)
		 */
		@Override
		public void sessionOpened(IoSession arg0) throws Exception
		{
			log.debug("session opened!");
		}

		/* (non-Javadoc)
		 * @see org.apache.mina.common.IoHandlerAdapter#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
		 */
		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception
		{
			if (log.isDebugEnabled())
				log.debug("Message received:  " + msg);
			
			String line = (String)msg;
			
			// need at least 5 characters... really need more though
			if (line.length() < 5)
			{
				session.write("500 Invalid command");
				return;
			}
			
			// check for "get "
			String getPart = line.substring(0,4);
			if (!getPart.toLowerCase().equals("get "))
			{
				session.write("500 Invalid command");
				return;
			}

			// strip off the "get "
			// get<SPACE>STUFF<NEWLINE>
			line = line.substring(4, line.length());
			
			boolean accepted = injector.accept(line);
			if (accepted)
			{
				int smtpPort = smtpManagement.getPort();
				InetAddress binding = smtpManagement.getBinding();
				if (binding == null)
				{
					binding = InetAddress.getLocalHost();
				}

				session.write("200 smtp:[" + binding.getHostAddress() + "]:" + smtpPort);
			}
			else
			{
				session.write("500 Lookup failed for: " + line);
			}
		}
	}
	
	/** */
//	public class CodecFactory implements ProtocolCodecFactory
//	{
//		TextLineEncoder encoder = new TextLineEncoder();
//		TextLineDecoder decoder = new TextLineDecoder();
//
//		public ProtocolDecoder getDecoder() throws Exception
//		{
//			return this.decoder;
//		}
//
//		public ProtocolEncoder getEncoder() throws Exception
//		{
//			return this.encoder;
//		}
//	}
	
	/** */
	public void start() throws IOException
	{
		if (this.acceptor != null)
			throw new IllegalStateException("TcpTableService already running");
		
		InetAddress binding = null;
		
		String bindAddress = System.getProperty("jboss.bind.address");
		if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
			binding = InetAddress.getByName(bindAddress);

		log.info("Starting TcpTableService: " + (binding==null ? "*" : binding) + ":" + port);
		
		this.acceptor = new SocketAcceptor();
		this.acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(
						new TextLineCodecFactory(Charset.forName("UTF-8"))));
		
		this.acceptor.bind(new InetSocketAddress(binding, this.port), new Handler());
	}

	/** */
	public void stop()
	{
		log.info("Stopping TcpTable service");
		this.acceptor.unbindAll();
		this.acceptor = null;
	}

	/** */
	public void setPort(int port)
	{
		if (this.acceptor != null)
			throw new IllegalStateException("TcpTable already running");
		
		this.port = port;
	}

	/** */
	public int getPort()
	{
		return this.port;
	}

	/** */
	public void setHostName(String hostname)
	{
		if (this.acceptor != null)
			throw new IllegalStateException("TcpTable already running");
		
		this.hostName = hostname;
	}

	/** */
	public String getHostName()
	{
		return this.hostName;
	}
}

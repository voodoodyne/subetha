package org.subethamail.core.postfix;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.inject.Inject;

import lombok.extern.java.Log;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.subethamail.core.injector.i.Injector;
import org.subethamail.core.smtp.SMTPService;

/**
 * @author Jon Stevens
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Startup
@Log
public class PostfixTcpTableService implements PostfixTcpTableManagement
{
	@Inject Injector injector;
	@Inject SMTPService smtpService;

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
		public void sessionOpened(IoSession session) throws Exception
		{
		    log.log(Level.FINE,"session opened!");
			
			session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
		}

		/* (non-Javadoc)
		 * @see org.apache.mina.common.IoHandlerAdapter#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
		 */
		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception
		{
		    log.log(Level.FINE,"Message received:  {0}", msg);
			
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
				int smtpPort = smtpService.getPort();
				
				InetAddress binding = smtpService.getBinding();
				if (binding == null)
					binding = InetAddress.getLocalHost();

				session.write("200 smtp:[" + binding.getHostAddress() + "]:" + smtpPort);
			}
			else
			{
				session.write("500 Lookup failed for: " + line);
			}
		}
		
		/* (non-Javadoc)
		 * @see org.apache.mina.common.IoHandlerAdapter#exceptionCaught(org.apache.mina.common.IoSession, java.lang.Throwable)
		 */
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
		    log.log(Level.INFO,"Closing due to exception", cause);
			
			session.close();
		}

		/* (non-Javadoc)
		 * @see org.apache.mina.common.IoHandlerAdapter#sessionIdle(org.apache.mina.common.IoSession, org.apache.mina.common.IdleStatus)
		 */
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
		    log.log(Level.FINE,"Closing idle connection with status {0}", status);
			
			session.close();
		}
	}
	
	/** */
	@PostConstruct
	public void start() throws IOException
	{
		if (this.acceptor != null)
			throw new IllegalStateException("TcpTableService already running");
		
		InetAddress binding = null;
		
		String bindAddress = System.getProperty("jboss.bind.address");
		if (bindAddress != null && !"0.0.0.0".equals(bindAddress))
			binding = InetAddress.getByName(bindAddress);

		log.log(Level.INFO,"Starting TcpTableService: {0}:{1}", new Object[]{(binding==null ? "*" : binding),port});
		
		this.acceptor = new SocketAcceptor();
		this.acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(
						new TextLineCodecFactory(Charset.forName("UTF-8"))));
		
		this.acceptor.bind(new InetSocketAddress(binding, this.port), new Handler());
	}

	/** */
	@PreDestroy
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

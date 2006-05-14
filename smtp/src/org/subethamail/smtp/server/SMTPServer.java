package org.subethamail.smtp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.i.MessageListener;

/**
 * Main SMTPServer class
 *
 * @author Jon Stevens
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
@SuppressWarnings("serial")
public class SMTPServer implements Runnable
{
	private static Log log = LogFactory.getLog(SMTPServer.class);

	private String hostName;
	private InetAddress bindAddress;
	private int port;
	private Map<MessageListener, MessageListener> listeners;

	private CommandHandler commandHandler;
	
	private ServerSocket serverSocket;
	private boolean go = false;
	private Thread thread;

	public SMTPServer(String hostname, InetAddress bindAddress, int port, Map<MessageListener, MessageListener> listeners) 
		throws UnknownHostException
	{
		this.hostName = hostname;
		this.port = port;
		this.listeners = listeners;

		this.commandHandler = new CommandHandler();
	}

	public void start()
	{
		if (thread != null)
			throw new IllegalStateException("SMTPServer already started");

		thread = new Thread(this, SMTPServer.class.getName());
		thread.start();
	}

	public void run()
	{
		try
		{
			if (this.bindAddress == null)
				serverSocket = new ServerSocket(this.port, 50);
			else
				serverSocket = new ServerSocket(this.port, 50, this.bindAddress);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		go = true;
		while (go)
		{
			try
			{
				ConnectionHandler connectionHandler = new ConnectionHandler(this, serverSocket.accept());
				connectionHandler.start();
			}
			catch (IOException ioe)
			{
				
//				Avoid this exception when shutting down.
//				20:34:50,624 ERROR [STDERR]     at java.net.PlainSocketImpl.socketAccept(Native Method)
//				20:34:50,624 ERROR [STDERR]     at java.net.PlainSocketImpl.accept(PlainSocketImpl.java:384)
//				20:34:50,624 ERROR [STDERR]     at java.net.ServerSocket.implAccept(ServerSocket.java:450)
//				20:34:50,624 ERROR [STDERR]     at java.net.ServerSocket.accept(ServerSocket.java:421)
//				20:34:50,624 ERROR [STDERR]     at org.subethamail.smtp2.SMTPServer.run(SMTPServer.java:92)
//				20:34:50,624 ERROR [STDERR]     at java.lang.Thread.run(Thread.java:613)
				if (go)
				{
					log.error(ioe.toString());
				}
			}
		}

		try
		{
			if (serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed())
				serverSocket.close();
			log.info("SMTP Server socket shut down.");
		}
		catch (IOException e)
		{
			log.error("Failed to close server socket.", e);
		}
	}

	public void stop()
	{
		go = false;
		this.thread = null;
		// force a socket close for good measure
		try
		{
			if (serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed())
				serverSocket.close();
		}
		catch (IOException e)
		{
		}
	}
	
	public String getHostName()
	{
		return hostName;
	}

	public String getVersion()
	{
		return "1.0a1";
	}
	
	public String getName()
	{
		return "SubethaMail Server";
	}

	public Map<MessageListener, MessageListener> getListeners()
	{
		return listeners;
	}

	public CommandHandler getCommandHandler()
	{
		return this.commandHandler;
	}
}
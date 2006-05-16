package org.subethamail.smtp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The thread that handles a connection
 * 
 * @author Jon Stevens
 */
public class ConnectionHandler extends Thread implements ConnectionContext
{
	private static Log log = LogFactory.getLog(ConnectionHandler.class);

	private SMTPServer server;
	private Session session;

	private InputStream input;
	private OutputStream output;

	private BufferedReader reader;
	private PrintWriter writer;

	private Socket socket;

	private long startTime;
	private long lastActiveTime;

	private boolean dataMode = false;
	
	public ConnectionHandler(SMTPServer server, Socket socket)
		throws IOException
	{
		super(server.getConnectionGroup(), ConnectionHandler.class.getName());
		this.server = server;
		this.socket = socket;

		input = socket.getInputStream();
		output = socket.getOutputStream();
		
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new PrintWriter(output);
		
		startTime = System.currentTimeMillis();
		lastActiveTime = startTime;
	}
	
	public Session getSession()
	{
		return this.session;
	}
	
	public ConnectionHandler getConnection()
	{
		return this;
	}
	
	public SMTPServer getServer()
	{
		return this.server;
	}

	public void timeout() throws IOException
	{
		try
		{
			this.sendResponse("421 Timeout waiting for data from client.");
		}
		finally
		{
			closeConnection();
		}
	}

	public void run()
	{
		if (log.isDebugEnabled())
			log.debug("SMTP connection count: " + server.getNumberOfConnections());

		session = new Session();
		try
		{
			if (this.server.hasTooManyConnections())
			{
				if (log.isDebugEnabled())
					log.debug("SMTP Too many connections!");

				this.sendResponse("554 Transaction failed. Too many connections.");
				return;
			}

			this.sendResponse("220 " + server.getHostName() + " ESMTP " + server.getName());

			while (session.isActive())
			{
				String command = reader.readLine();
				this.server.getCommandHandler().handleCommand(this, command);
				lastActiveTime = System.currentTimeMillis();
			}
		}
		catch (IOException e1)
		{
			if (log.isDebugEnabled())
				log.debug(e1);
		}
		finally
		{
			closeConnection();
		}
	}

	private void closeConnection()
	{
		try
		{
			try
			{
				writer.close();
				input.close();
			}
			finally
			{
				if (socket != null && socket.isBound() && !socket.isClosed())
					socket.close();
			}
		}
		catch (IOException e)
		{
			log.debug(e);
		}
	}

	public Socket getSocket()
	{
		return this.socket;
	}

	public InputStream getInput()
	{
		return this.input;
	}

	public OutputStream getOutput()
	{
		return this.output;
	}

	public BufferedReader getReader()
	{
		return this.reader;
	}

	public PrintWriter getWriter()
	{
		return this.writer;
	}

	public void sendResponse(String response) throws IOException
	{
		this.writer.print(response + "\r\n");
		this.writer.flush();
	}
	
	public long getStartTime()
	{
		return this.startTime;
	}

	public long getLastActiveTime()
	{
		return this.lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime)
	{
		this.lastActiveTime = lastActiveTime;
	}
}

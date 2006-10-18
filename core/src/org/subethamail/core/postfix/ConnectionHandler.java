package org.subethamail.core.postfix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.core.postfix.io.CRLFTerminatedReader;
import org.subethamail.core.postfix.io.LastActiveInputStream;

/**
 * The thread that handles a connection. This class
 * passes most of it's responsibilities off to the
 * CommandHandler.
 * 
 * @author Jon Stevens
 */
public class ConnectionHandler extends Thread implements ConnectionContext
{
	private static Log log = LogFactory.getLog(ConnectionHandler.class);

	private TcpTableServer server;

	private InputStream input;
	private OutputStream output;

	private CRLFTerminatedReader reader;
	private PrintWriter writer;

	private Socket socket;

	private boolean active = true;
	private long startTime;
	private long lastActiveTime;
	
	public ConnectionHandler(TcpTableServer server, Socket socket)
		throws IOException
	{
		super(server.getConnectionGroup(), ConnectionHandler.class.getName());
		this.server = server;
		this.socket = socket;

		this.startTime = System.currentTimeMillis();
		this.lastActiveTime = this.startTime;
		
		this.input = new LastActiveInputStream(socket.getInputStream(), this);
		this.output = socket.getOutputStream();
		
		this.reader = new CRLFTerminatedReader(this.input);
		this.writer = new PrintWriter(this.output);
	}
	
	public ConnectionHandler getConnection()
	{
		return this;
	}
	
	public TcpTableServer getServer()
	{
		return this.server;
	}

	public void timeout() throws IOException
	{
		try
		{
			this.sendResponse("400 Timeout waiting for data from client.");
		}
		finally
		{
			closeConnection();
		}
	}

	public void run()
	{
		if (log.isDebugEnabled())
			log.debug("TcpTableServer connection count: " + this.server.getNumberOfConnections());

		try
		{
			if (this.server.hasTooManyConnections())
			{
				log.debug("TcpTableServer: Too many connections!");

				this.sendResponse("400 Transaction failed. Too many connections.");
				return;
			}

			while (this.isActive())
			{
				// read a line off the stream.
				String line = this.reader.readLine();

				// need at least 5 characters... really need more though
				if (line.length() < 5)
				{
					this.sendResponse("500 Invalid command");
					continue;
				}
				
				// check for "get "
				String getPart = line.substring(0,4);
				if (!getPart.toLowerCase().equals("get "))
				{
					this.sendResponse("500 Invalid command");
					continue;
				}

				// strip off the "get "
				// get<SPACE>STUFF<NEWLINE>
				line = line.substring(4, line.length());
				
				TcpTableService service = this.server.getTcpTableService();
				boolean accepted = service.injector.accept(line);
				if (accepted)
				{
					int port = service.smtpManagement.getPort();
					InetAddress binding = service.smtpManagement.getBinding();
					if (binding == null)
					{
						binding = InetAddress.getLocalHost();
					}

					this.sendResponse("200 smtp:[" + binding.getHostAddress() + "]:" + port);
				}
				else
				{
					this.sendResponse("500 Lookup failed for: " + line);
				}
				lastActiveTime = System.currentTimeMillis();
			}
		}
		catch (IOException e1)
		{
			try
			{
				// primarily if things fail during the MessageListener.deliver(), then try
				// to send a temporary failure back so that the server will try to resend 
				// the message later.
				this.sendResponse("400 Problem attempting to execute commands. Please try again later.");
			}
			catch (IOException e)
			{
			}
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
				this.writer.close();
				this.input.close();
			}
			finally
			{
				closeSocket();
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

	private void closeSocket() throws IOException
	{
		if (this.socket != null && this.socket.isBound() && !this.socket.isClosed())
			this.socket.close();
	}

	public InputStream getInput()
	{
		return this.input;
	}

	public OutputStream getOutput()
	{
		return this.output;
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

	public void refreshLastActiveTime()
	{
		this.lastActiveTime = System.currentTimeMillis();
	}

	public boolean isActive()
	{
		return this.active;
	}

	public void quit()
	{
		this.active = false;
	}
}

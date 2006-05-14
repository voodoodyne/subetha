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
import org.subethamail.smtp.SMTPServer;

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
	
	public ConnectionHandler(SMTPServer server, Socket socket)
		throws IOException
	{
		super(ConnectionHandler.class.getName());
		this.server = server;
		this.socket = socket;

		input = socket.getInputStream();
		output = socket.getOutputStream();
		
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new PrintWriter(output);
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

	public void run()
	{
		session = new Session();
		
		try
		{
			this.sendResponse("220 " + server.getHostname() + " ESMTP " + server.getName());

			while (session.isActive())
			{
				String command = null;
				command = reader.readLine();
				this.server.getCommandHandler().handleCommand(this, command);
				if (command == null)
				{
					session.quit();
				}
				else
				{
					writer.flush();
				}
			}
		}
		catch (IOException e1)
		{
			log.debug(e1);
		}
		finally
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
					socket.close();
				}
			}
			catch (IOException e)
			{
				log.debug(e);
			}
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
}

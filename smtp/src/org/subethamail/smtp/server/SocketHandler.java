package org.subethamail.smtp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
class SocketHandler implements Runnable
{
	private SMTPServerContext serverContext;

	private final Socket socket;

	private Session session;

	private BufferedReader in;

	private PrintWriter out;

	public SocketHandler(SMTPServerContext serverContext, Socket aSocket)
			throws IOException
	{
		this.serverContext = serverContext;
		this.socket = aSocket;
		session = new Session(serverContext, socket);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		new Thread(this).start();
	}

	public void run()
	{
		// TODO(imf): Refactor
		try
		{
			out.print("220 " + serverContext.getHostname()
					+ " ESMTP SubethaMail" + "\r\n");
// Postfix doesn't output this, so why are we?
//			try
//			{
//				out.print("220 You are "
//						+ serverContext
//								.resolveHost(session.getRemoteHostname())
//						+ "\r\n");
//			}
//			catch (IOException e)
//			{
//				session.quit();
//				out.print("221 " + serverContext.getHostname()
//						+ " closing connection. " + e.getMessage() + "\r\n");
//			}
		}
		catch (Exception e)
		{
			session.quit();
			out.print("221 " + serverContext.getHostname()
					+ " closing connection. " + e.getMessage() + "\r\n");
		}
		out.flush();
		while (session.isActive())
		{
			String command = null;
			try
			{
				command = (in.readLine());
			}
			catch (IOException ioe)
			{
				session.quit();
			}
			if (command == null)
			{
				session.quit();
			}
			else
			{
				command = command.trim();
				out.print(serverContext.getCommandDispatcher().executeCommand(
						command, session));
				out.print("\r\n");
				out.flush();
			}
		}
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			// Noop.
		}
		out.close();
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			// Noop.
		}
	}
}

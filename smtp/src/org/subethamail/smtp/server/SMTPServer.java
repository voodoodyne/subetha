package org.subethamail.smtp.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import org.subethamail.smtp.command.CommandDispatcher;
import org.subethamail.smtp.command.DataCommand;
import org.subethamail.smtp.command.ExpnCommand;
import org.subethamail.smtp.command.HelloCommand;
import org.subethamail.smtp.command.HelpCommand;
import org.subethamail.smtp.command.MailCommand;
import org.subethamail.smtp.command.NoopCommand;
import org.subethamail.smtp.command.QuitCommand;
import org.subethamail.smtp.command.ReceiptCommand;
import org.subethamail.smtp.command.ResetCommand;
import org.subethamail.smtp.command.VerboseCommand;
import org.subethamail.smtp.command.VerifyCommand;
import org.subethamail.smtp.i.MessageListener;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class SMTPServer implements SMTPServerContext
{
	private final String hostname;

	private int port;

	private List<String> validRecipientHosts; // TODO(imf): Rename this

	// variable to something more
	// standard and descriptive.
	private List<MessageListener> listeners;

	CommandDispatcher commandDispatcher;

	private boolean hostResolutionEnabled = true;

	private boolean recipientDomainFilteringEnabled;

	public SMTPServer(String hostname, int port)
	{
		this.hostname = hostname;
		this.port = port;
		validRecipientHosts = new ArrayList<String>();
		listeners = new ArrayList<MessageListener>();
		addRecipientHost(hostname);
		initializeCommandDispatcher();
	}

	private void initializeCommandDispatcher()
	{
		commandDispatcher = new CommandDispatcher(this);
		new HelloCommand(commandDispatcher);
		// new EhloCommand(commandDispatcher);
		new MailCommand(commandDispatcher);
		new ReceiptCommand(commandDispatcher);
		new DataCommand(commandDispatcher);
		new ResetCommand(commandDispatcher);
		new NoopCommand(commandDispatcher);
		new QuitCommand(commandDispatcher);
		new HelpCommand(commandDispatcher);
		new VerifyCommand(commandDispatcher);
		new ExpnCommand(commandDispatcher);
		new VerboseCommand(commandDispatcher);
		// -- new EtrnCommand(commandDispatcher);
		// new DsnCommand(commandDispatcher);
	}

	public void addRecipientHost(String hostname)
	{
		validRecipientHosts.add(hostname);
	}

	public String getServerVersion()
	{
		return "1.0a1";
	}

	public String getHostname()
	{
		return hostname;
	}

	public List<String> getValidRecipientHosts()
	{
		return validRecipientHosts;
	}

	ServerSocket ss;

	boolean go = false;

	public void start() throws IOException
	{
		ss = new ServerSocket(port);
		go = true;
		while (go)
		{
			try
			{
				new SocketHandler(this, ss.accept());
			}
			catch (IOException ioe)
			{
				System.err.println(ioe.toString());
				ioe.printStackTrace();
			}
		}
	}

	public void stop()
	{
		go = false;
	}

	public String resolveHost(String hostname) throws IOException,
			ServerRejectedException
	{
		if (hostResolutionEnabled)
		{
			return hostname.trim() + "/"
					+ InetAddress.getByName(hostname).getHostAddress();
		}
		else
		{
			return hostname;
		}
	}

	public void setCommandDispatcher(CommandDispatcher commandDispatcher)
	{
		this.commandDispatcher = commandDispatcher;
	}

	public CommandDispatcher getCommandDispatcher()
	{
		return commandDispatcher;
	}

	public void setRecipientDomainFilteringEnabled(boolean state)
	{
		recipientDomainFilteringEnabled = state;
	}

	public void register(MessageListener listener)
	{
		listeners.add(listener);
	}

	public void deregister(MessageListener listener)
	{
		listeners.remove(listener);
	}

	public boolean accept(String from, String recipient)
	{
		for (MessageListener messageListener : listeners)
		{
			if (messageListener.accept(from, recipient))
				return true;
		} // else
		return false;
	}

	public void deliver(String from, String recipient, InputStream data)
			throws IOException
	{
		for (MessageListener messageListener : listeners)
		{
			if (messageListener.accept(from, recipient))
			{
				messageListener.deliver(from, recipient, data);
			}
		}
	}

	public int getPort()
	{
		return port;
	}

	public void disableHostResolution()
	{
		hostResolutionEnabled = false;
	}

	public void enableHostResolution()
	{
		hostResolutionEnabled = true;
	}

	public boolean isHostResolutionEnabled()
	{
		return hostResolutionEnabled;
	}

	public boolean getRecipientDomainFilteringEnabled()
	{
		return recipientDomainFilteringEnabled;
	}
}

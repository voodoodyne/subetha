package org.subethamail.smtp.server;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.command.DataCommand;
import org.subethamail.smtp.command.EhloCommand;
import org.subethamail.smtp.command.HelloCommand;
import org.subethamail.smtp.command.HelpCommand;
import org.subethamail.smtp.command.MailCommand;
import org.subethamail.smtp.command.QuitCommand;
import org.subethamail.smtp.command.ReceiptCommand;
import org.subethamail.smtp.command.ResetCommand;

/**
 * This class manages execution of a SMTP command.
 * 
 * @author Jon Stevens
 */
public class CommandHandler
{
	private Map<String, Command> commandMap = new HashMap<String, Command>();
	private static Log log = LogFactory.getLog(CommandHandler.class);

	public CommandHandler()
	{
		addCommand(new DataCommand());
		addCommand(new EhloCommand());
		addCommand(new HelloCommand());
		addCommand(new HelpCommand());
		addCommand(new MailCommand());
		addCommand(new QuitCommand());
		addCommand(new ReceiptCommand());
		addCommand(new ResetCommand());
	}

	public void addCommand(Command command)
	{
		if (log.isDebugEnabled())
			log.debug("Added command: " + command.getName());
		this.commandMap.put(command.getName(), command);
	}

	public void gotConnected(ConnectionContext context)
			throws SocketTimeoutException, IOException
	{
		if (log.isDebugEnabled())
			log.debug(context.getServer().getName() + " Connection opened: "
				+ context.getSocket().getInetAddress());
	}

	public void lostConnection(ConnectionContext context) throws IOException
	{
		if (log.isDebugEnabled())
			log.debug(context.getServer().getName() + " Connection lost: "
				+ context.getSocket().getInetAddress());
	}

	public void closingConnection(ConnectionContext context) throws IOException
	{
		if (log.isDebugEnabled())
			log.debug(context.getServer().getName() + " Connection closed: "
				+ context.getSocket().getInetAddress());
	}

	public void handleMaxAuthTry(ConnectionContext context) throws IOException
	{
		// TODO Auto-generated method stub
	}

	public boolean handleMaxConnection(ConnectionContext context) throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void handleTimeout(ConnectionContext context) throws SocketException, IOException
	{
		// TODO Auto-generated method stub		
	}

	public void handleCommand(ConnectionContext context, String commandString)
		throws SocketTimeoutException, IOException
	{
		try
		{
			Command command = getCommandFromString(commandString);
			command.execute(commandString, context);
		}
		catch (CommandException e)
		{
			context.sendResponse("500 " + e.getMessage());
		}
	}
	
	private Command getCommandFromString(String commandString)
			throws UnknownCommandException, InvalidCommandNameException
	{
		Command command = null;
		String key = toKey(commandString);
		if (key != null)
		{
			command = commandMap.get(key);
		}
		if (command == null)
		{
			throw new UnknownCommandException("Error: command not implemented");
		}
		return command;
	}

	private String toKey(String string) throws InvalidCommandNameException
	{
		if (string == null || string.length() < 4)
			throw new InvalidCommandNameException("Error: bad syntax");

		return string.substring(0, 4).toUpperCase();
	}
}

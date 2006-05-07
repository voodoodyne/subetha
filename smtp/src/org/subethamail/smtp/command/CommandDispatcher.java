package org.subethamail.smtp.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.server.SMTPServerContext;
import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class CommandDispatcher
{
	public static final Log log = LogFactory.getLog(CommandDispatcher.class);

	private Map<String, Command> commands = new HashMap<String, Command>();

	private List<Command> commandList = new ArrayList<Command>();

	private SMTPServerContext SMTPServerContext;

	public CommandDispatcher(SMTPServerContext SMTPServerContext)
	{
		this.SMTPServerContext = SMTPServerContext;
	}

	public String executeCommand(String commandString, Session session)
	{
		if (session.isDataMode())
		{
			return handleDataMode(commandString, session);
		}
		Command command = null;
		try
		{
			command = getCommandFromString(commandString);
		}
		catch (CommandException e)
		{
			return "500 Command unrecognized: \"" + commandString + "\"";
		}
		return command.execute(commandString, session);
	}

	private String handleDataMode(String commandString, Session session)
	{
		if (".".equals(commandString))
		{
			session.flush(SMTPServerContext);
			return "250 Ok: message passed to handler";
		}
		if (commandString.startsWith("."))
		{
			commandString = commandString.substring(1);
		}
		session.addData(commandString);
		return "";
	}

	private String getMessageId(Session session)
	{
		return session.generateMessageId();
	}

	private Command getCommandFromString(String commandString)
			throws UnknownCommandException, InvalidCommandNameException
	{
		Command command = commands.get(toKey(commandString));
		if (command == null)
		{
			throw new UnknownCommandException("Command " + toKey(commandString)
					+ " not found.");
		}
		return command;
	}

	public void add(String name, Command command)
			throws InvalidCommandNameException
	{
		commands.put(toKey(name), command);
		commandList.add(command);
	}

	public String toKey(String string) throws InvalidCommandNameException
	{
		if (string == null || string.length() < 4)
			throw new InvalidCommandNameException();
		return string.substring(0, 4).toUpperCase();
	}

	public HelpMessage getHelpMessage(String command)
			throws InvalidCommandNameException, UnknownCommandException
	{
		return getCommandFromString(command).getHelp();
	}

	public List<Command> getCommandList()
	{
		return commandList;
	}

	public void setServerContext(SMTPServerContext SMTPServer)
	{
		this.SMTPServerContext = SMTPServer;
	}

	public SMTPServerContext getServerContext()
	{
		return SMTPServerContext;
	}

	public void replaceWith(Command oldCommand, Command newCommand)
	{
		if (!oldCommand.getName().equals(newCommand.getName()))
		{
			log.error("Attempt to replace " + oldCommand.getName() + ":"
					+ oldCommand + " with " + newCommand.getName() + ":"
					+ newCommand);
			return;
		}
		ArrayList<Command> newCommandList = new ArrayList<Command>(commandList
				.size());
		for (Command command : commandList)
		{
			newCommandList.add(command.equals(oldCommand) ? newCommand
					: command);
		}
		commandList = newCommandList;
		commands.put(oldCommand.getName(), newCommand);
	}
}

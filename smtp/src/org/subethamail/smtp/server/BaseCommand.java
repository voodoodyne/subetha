package org.subethamail.smtp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
abstract public class BaseCommand implements Command
{
	// TODO(imf): Extract commandRegistry into its own class. Inject it.
	private String name;
	private static Map<String, HelpMessage> helpMessageMap = new HashMap<String, HelpMessage>();

	public BaseCommand(String name, String help)
	{
		this.name = name;
		setHelp(new HelpMessage(name, help));
	}

	public BaseCommand(String name, String help, String argumentDescription)
	{
		this.name = name;
		setHelp(new HelpMessage(name, help, argumentDescription));
	}
	
	abstract public void execute(String commandString, ConnectionContext context) throws IOException;

	public void setHelp(HelpMessage helpMessage)
	{
		helpMessageMap.put(helpMessage.getName().toUpperCase(), helpMessage);
	}

	public HelpMessage getHelp(String commandName)
		throws CommandException
	{
		HelpMessage msg = helpMessageMap.get(commandName.toUpperCase());
		if (msg == null)
			throw new CommandException();
		return msg;
	}

	public Map<String, HelpMessage> getHelp()
	{
		return helpMessageMap;
	}

	protected String getArgPredicate(String commandString)
	{
		if (commandString == null)
			return "";
		
		return commandString.substring(4).trim();
	}

	public String getName()
	{
		return name;
	}

	protected boolean isValidEmailAddress(String address)
	{
		// TODO(imf): Make this more robust.
		return address.indexOf("@") > 0;
	}

	protected String[] getArgs(String commandString)
	{
		List<String> strings = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(commandString);
		while (stringTokenizer.hasMoreTokens())
		{
			strings.add(stringTokenizer.nextToken());
		}
		return strings.toArray(new String[strings.size()]);
	}

	protected String extractEmailAddress(String args, final int subcommandOffset)
	{
		String address = args.substring(subcommandOffset).trim();
		if (address.indexOf('<') == 0)
			address = address.substring(1, address.indexOf('>'));
		return address;
	}
}

package org.subethamail.smtp.command;

import org.subethamail.smtp.server.SMTPServerContext;
import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
public class HelloCommand extends BaseCommand
{
	public HelloCommand(CommandDispatcher commandDispatcher)
	{
		super(commandDispatcher, "HELO");
		helpMessage = new HelpMessage("HELO", "<hostname>",
				"Introduce yourself.");
	}

	public String execute(String commandString, Session session)
	{
		String[] args = getArgs(commandString);
		if (args.length < 2)
		{
			return "501 Syntax: HELO <hostname>";
		}
		String remoteHost = args[1];
		
		// http://cr.yp.to/smtp/helo.html#helo
		// "I recommend that they use bracketed IP addresses:"
//		If we cared about the remoteHost IP, we would deal with it here.
//		remoteHost = remoteHost.replace("[", "");
//		remoteHost = remoteHost.replace("]", "");

		if (!session.hasSeenHelo())
		{
			SMTPServerContext serverContext = commandDispatcher.getServerContext();
			session.setHasSeenHelo(true);
			return new StringBuilder().append("250 ")
				.append(serverContext.getHostname()).toString();
		}
		else
		{
			return "503 " + remoteHost + " Duplicate HELO";
		}
	}
}

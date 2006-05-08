package org.subethamail.smtp.command;

import org.subethamail.smtp.server.SMTPServerContext;
import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
public class EhloCommand extends BaseCommand
{
	public EhloCommand(CommandDispatcher commandDispatcher)
	{
		super(commandDispatcher, "EHLO");
		helpMessage = new HelpMessage("EHLO", "<hostname>",
				"Introduce yourself.");
	}

	public String execute(String commandString, Session session)
	{
		String[] args = getArgs(commandString);
		if (args.length < 2)
		{
			return "501 Syntax: EHLO <hostname>";
		}
		String remoteHost = args[1];
		
		// http://cr.yp.to/smtp/helo.html#helo
		// "I recommend that they use bracketed IP addresses:"
//		If we cared about the remoteHost IP, we would deal with it here.
//		remoteHost = remoteHost.replace("[", "");
//		remoteHost = remoteHost.replace("]", "");

//		postfix returns...
//		250-server.host.name
//		250-PIPELINING
//		250-SIZE 10240000
//		250-ETRN
//		250 8BITMIME
		if (!session.hasSeenHelo())
		{
			SMTPServerContext serverContext = commandDispatcher.getServerContext();

			session.setHasSeenHelo(true);
			return new StringBuilder().append("250-")
				.append(serverContext.getHostname())
//  We don't understand the SIZE or PIPELINING options, so don't tell people we do.
//				.append("\r\n")
//				.append("250-PIPELINING")
//				.append("\r\n")
//				.append("250-SIZE 10240000")
				.append("\r\n")
				.append("250 8BITMIME")
				.toString();
		}
		else
		{
			return "503 " + remoteHost + " Duplicate EHLO";
		}
	}
}

package org.subethamail.smtp.command;

import java.io.IOException;
import org.subethamail.smtp.server.SMTPServerContext;
import org.subethamail.smtp.server.ServerRejectedException;
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
		remoteHost = remoteHost.replace("[", "");
		remoteHost = remoteHost.replace("]", "");
		
		if (session.getDeclaredRemoteHostname() == null)
		{
			final SMTPServerContext SMTPServerContext = commandDispatcher
					.getServerContext();
			try
			{
				final String fullyQualifiedRemoteHost = SMTPServerContext
						.resolveHost(remoteHost);
				session.setDeclaredRemoteHostname(fullyQualifiedRemoteHost);

//				postfix returns...
//				250-server.host.name
//				250-PIPELINING
//				250-SIZE 10240000
//				250-ETRN
//				250 8BITMIME
				return new StringBuilder().append("250-")
					.append(SMTPServerContext.getHostname())
					.append("\r\n")
					.append("250-PIPELINING")
					.append("\r\n")
					.append("250-SIZE 10240000")
					.append("\r\n")
					.append("250 8BITMIME")
					.toString();
			}
			catch (IOException e)
			{
				return "501 Unknown host: " + remoteHost;
			}
			catch (ServerRejectedException e)
			{
				session.quit();
				return "221 " + remoteHost + " closing connection. "
						+ e.getMessage();
			}
		}
		else
		{
			return "503 " + remoteHost + " Duplicate EHLO";
		}
	}
}

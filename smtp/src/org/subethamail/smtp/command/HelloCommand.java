package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;
import org.subethamail.smtp.i.SMTPServerContext;
import org.subethamail.smtp.server.ServerRejectedException;

import java.io.IOException;

import org.subethamail.smtp.command.BaseCommand;
import org.subethamail.smtp.command.CommandDispatcher;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class HelloCommand extends BaseCommand {
  public HelloCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "HELO");
    helpMessage = new HelpMessage("HELO", "<hostname>", "Introduce yourself.");
  }

  public String execute(String commandString, Session session) {
    String[] args = getArgs(commandString);
    if (args.length < 2) {
      return "501 Syntax: HELO <hostname>";
    }
    final String remoteHost = args[1];

    if (session.getDeclaredRemoteHostname() == null) {
      final SMTPServerContext SMTPServerContext = commandDispatcher.getServerContext();
      try {
        final String fullyQualifiedRemoteHost = SMTPServerContext.resolveHost(remoteHost);
        session.setDeclaredRemoteHostname(fullyQualifiedRemoteHost);
        return new StringBuilder().append("250 ")
          .append(SMTPServerContext.getHostname())
            .append(" Hello ")
            .append(fullyQualifiedRemoteHost)
            .toString();
      } catch (IOException e) {
        return "501 Unknown host: " + remoteHost;
      } catch (ServerRejectedException e) {
        session.quit();
        return "221 test.subethamail.org closing connection. " + e.getMessage();
      }
    } else {
      return "503 " + remoteHost + " Duplicate HELO/EHLO";
    }
  }
}

package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

import java.util.StringTokenizer;

import org.subethamail.smtp.command.Command;
import org.subethamail.smtp.command.CommandDispatcher;
import org.subethamail.smtp.command.HelpMessage;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class MailCommand extends Command {

  public MailCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "MAIL");
    helpMessage = new HelpMessage("MAIL", "FROM: <sender> [ <parameters> ]",
        "Specifies the sender. Parameters are ESMTP extensions.\n" +
        "See \"HELP DSN\" for details.");
  }

  @Override
  public String execute(String commandString, Session session) {
    if (session.getSender() != null) {
      return "503 Sender already specified.";
    } else {
      String[] args = getArgs(commandString);
      StringTokenizer stringTokenizer = new StringTokenizer(commandString);
      stringTokenizer.nextToken();
      if (args.length < 3
        || ! "FROM:".equalsIgnoreCase(args[1])
        || ! "MAIL".equalsIgnoreCase(args[0])) {
        return "501 Syntax: MAIL FROM: <address>  Error in parameters: \"" + getArgPredicate(commandString) + "\"";
      }
      final String emailAddress = args[2];
      if (isValidEmailAddress(emailAddress)) {
        session.setSender(emailAddress);
        return "250 <" + emailAddress + "> Sender ok.";
        // TODO(imf): Deal with DSN commands.
      } else {
        return "553 <" + emailAddress + "> Domain name required.";
      }
    }
  }

}

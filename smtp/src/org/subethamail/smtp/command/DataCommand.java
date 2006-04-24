package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;
import org.subethamail.smtp.command.BaseCommand;
import org.subethamail.smtp.command.CommandDispatcher;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DataCommand extends BaseCommand {
  public DataCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "DATA");
    helpMessage = new HelpMessage("DATA", "Following text is collected as the message.\n" +
        "End data with <CR><LF>.<CR><LF>");
  }

  public String execute(String commandString, Session session) {
    if (session.getSender() == null) {
      return "503 Need MAIL command.";
    }
    if (session.getRecipients().size() == 0) {
      return "503 Need RCPT (recipient)";
    }
    session.setDataMode(true);
    return "354 Enter mail, end with \".\" on a line by itself.";
  }
}

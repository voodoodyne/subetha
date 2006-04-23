package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;
import org.subethamail.smtp.command.Command;
import org.subethamail.smtp.command.CommandDispatcher;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class ExpnCommand extends Command {
  public ExpnCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "EXPN");
    helpMessage = new HelpMessage("EXPN", "<recipient>",
        "Expand an address. If the address is a mailing list, return\n" +
        "the contents of the list.\n" +
        "This command is often disabled for security reasons.");
  }

  @Override
  public String execute(String commandString, Session session) {
    return "502 Sorry, we do not allow this operation.";
  }

}

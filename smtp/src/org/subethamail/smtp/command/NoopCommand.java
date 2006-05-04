package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class NoopCommand extends BaseCommand {
  public NoopCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "NOOP");
    helpMessage = new HelpMessage("NOOP", "Do nothing.");
  }

  @Override
  public String execute(String commandString, Session session) {
    return "250 OK";
  }

}

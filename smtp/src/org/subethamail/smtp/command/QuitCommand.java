package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class QuitCommand extends BaseCommand {
  public QuitCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "QUIT");
    helpMessage = new HelpMessage("QUIT", "Exit the SMTP session.");
  }

  public String execute(String commandString, Session session) {
    session.quit();
    return "221 " + commandDispatcher.getServerContext().getHostname() + " closing connection.";
  }
}

package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: imf
 * Date: Apr 24, 2006
 * Time: 3:15:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandLogger implements Command {
    private final Command command;
    public static final Log log = LogFactory.getLog(CommandLogger.class);
    private Log commandLog;
    private String name;
    private HelpMessage helpMessage;

    public CommandLogger(Command command) {
        this.command = command;
        name = command.getName();
        log.info("Wrapping command " + command.getName() + " with CommandLogger.");
        commandLog = LogFactory.getLog(command.getClass());
        helpMessage = command.getHelp();
        command.replaceWith(this);
    }

    public String execute(String commandString, Session session) {
      String response = command.execute(commandString, session);
      commandLog.info(commandString + "\t=> " + response);
      return response;
    }

    public HelpMessage getHelp() {
        return helpMessage;
    }

    public String getName() {
        return name;
    }

  public void replaceWith(Command newCommand) {
    command.replaceWith(newCommand);
  }
}

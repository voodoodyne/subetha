package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;
import org.subethamail.smtp.i.SMTPServerContext;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.subethamail.smtp.command.Command;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class CommandDispatcher {
  private Map<String, Command> commands = new HashMap<String, Command>();
  private List<Command> commandList = new ArrayList<Command>();
  private SMTPServerContext SMTPServerContext;

  public CommandDispatcher(SMTPServerContext SMTPServerContext) {
    this.SMTPServerContext = SMTPServerContext;
  }

  public String executeCommand(String commandString, Session session) {
    if (session.isDataMode()) {
      return handleDataMode(commandString, session);
    }
    Command command = null;
    try {
      command = getCommandFromString(commandString);
    } catch (CommandException e) {
      return "500 Command unrecognized: \"" + commandString + "\"";
    }
    return command.execute(commandString, session);
  }

  private String handleDataMode(String commandString, Session session) {
    if (".".equals(commandString)) {
      session.flush(SMTPServerContext);
      return "250 Message ID <" + getMessageId(session) + "> accepted for delivery.";
    }
    if (commandString.startsWith(".")) {
      commandString = commandString.substring(1);
    }
    session.addData(commandString);
    return "";
  }

  private String getMessageId(Session session) {
    return session.generateMessageId();
  }

  private Command getCommandFromString(String commandString) throws UnknownCommandException, InvalidCommandNameException {
    Command command = commands.get(toKey(commandString));
    if (command == null) { throw new UnknownCommandException("Command " + toKey(commandString) + " not found."); }
    return command;
  }

  public void add(String name, Command command) throws InvalidCommandNameException {
    commands.put(toKey(name), command);
    commandList.add(command);
  }

  public String toKey(String string) throws InvalidCommandNameException {
    if (string == null || string.length() < 4) throw new InvalidCommandNameException();
    return string.substring(0,4).toUpperCase();
  }

  public HelpMessage getHelpMessage(String command) throws InvalidCommandNameException, UnknownCommandException {
    return getCommandFromString(command).getHelp();
  }

  public List<Command> getCommandList() {
    return commandList;
  }

  public void setServerContext(SMTPServerContext SMTPServer) {
    this.SMTPServerContext = SMTPServer;
  }

  public SMTPServerContext getServerContext() {
    return SMTPServerContext;
  }
}

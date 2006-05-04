package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class HelpCommand extends BaseCommand {

  public HelpCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "HELP");
    helpMessage = new HelpMessage("HELP", "[ <topic> ]", "The HELP command gives help info about the topic specified.\n" +
        "For a list of topics, type HELP by itself.");
  }

  @Override
  public String execute(String commandString, Session session) {
    String args = getArgPredicate(commandString);
    if ("".equals(args)) {
      return getCommandMessage();
    }
    try {
      return commandDispatcher.getHelpMessage(args).toOutputString();
    } catch (CommandException e) {
      return "504 HELP topic \"" + args + "\" unknown.";
    }
  }

  private String getCommandMessage() {
    return "214-This is the SubEthaMail SMTP Server version " + getServerVersion() + " running on " + getHostAddress() + "\r\n" +
        "214-Topics:\r\n" +
        getFormattedTopicList() +
        "214-For more info use \"HELP <topic>\".\r\n" +
        "214-For more information about this server, visit:\r\n" +
        "214-    http://subetha.tigris.org\r\n" +
        "214-To report bugs in the implementation, send email to:\r\n" +
        "214-    issues@subetha.tigris.org\r\n" +
        "214-For local information send email to Postmaster at your site.\r\n" +
        "214 End of HELP info";
  }

  private String getHostAddress() {
    return commandDispatcher.getServerContext().getHostname();
  }

  private String getServerVersion() {
    return commandDispatcher.getServerContext().getServerVersion();
  }

  protected String getFormattedTopicList() {
    int index = 0;
    StringBuilder sb = new StringBuilder();
    for (Command command : commandDispatcher.getCommandList()) {
      if (index++ % 5 == 0) {
        sb.append("\r\n214-");
      }
      sb.append("    ").append(command.getName());
    }
    sb.deleteCharAt(0);
    sb.deleteCharAt(0);
    sb.append("\r\n");
    return sb.toString();
  }

}

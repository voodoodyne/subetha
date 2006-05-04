package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class VerboseCommand extends BaseCommand {
  public VerboseCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "VERB");
    helpMessage =  new HelpMessage("VERB", "Go into verbose mode. This sends Oxy responses that are\n" +
        "not RFC821 standard (but should be). They are recognized\n" +
        "by humans and other SMTP implementations.");
  }

  @Override
  public String execute(String commandString, Session session) {
    session.setVerbose(true);
    return "250 Verbose mode";
  }

}

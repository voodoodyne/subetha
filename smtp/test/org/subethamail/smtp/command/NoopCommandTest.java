package org.subethamail.smtp.command;

import org.subethamail.smtp.command.CommandTestCase;
import org.subethamail.smtp.command.HelpMessageTest;
import org.subethamail.smtp.command.NoopCommand;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class NoopCommandTest extends CommandTestCase {

  public void testNoopCommand() throws Exception {
    new NoopCommand(commandDispatcher);
    assertEquals(OK, commandDispatcher.executeCommand("NOOP", session));
  }

  public void testNoopCommandHelp() throws Exception {
    new NoopCommand(commandDispatcher);
    assertEquals(HelpMessageTest.NOOP_HELP_OUTPUT, commandDispatcher.getHelpMessage("NOOP").toOutputString());
  }


}

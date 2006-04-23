package org.subethamail.smtp.command;

import org.subethamail.smtp.command.HelpMessage;
import org.subethamail.smtp.command.Command;
import org.subethamail.smtp.command.CommandDispatcher;
import org.subethamail.smtp.command.HelpCommand;
import junit.framework.Assert;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class CommandTest extends CommandTestCase {
  private static final HelpMessage TEST_HELP_MESSAGE = new HelpMessage("TEST", "No help for TEST.");

  public void testCommandRegistration() throws Exception {
    Command command = new Command(commandDispatcher, "TEST");
    Assert.assertEquals("500 Not Implemented", commandDispatcher.executeCommand("testing testing", session));
  }

  public void testHelp() throws Exception {
    CommandDispatcher commandDispatcher = new CommandDispatcher(new DummySMTPServerContext());
    new HelpCommand(commandDispatcher);
    Command command = new Command(commandDispatcher, "TEST");
    command.initHelpMessage();
    assertEquals(TEST_HELP_MESSAGE, command.getHelp());
    assertEquals(TEST_HELP_MESSAGE, commandDispatcher.getHelpMessage("TEST"));
    assertEquals("214-TEST\n" +
        "214-    No help for TEST.\n" +
        "214 End of TEST info", commandDispatcher.executeCommand("HELP TEST", session));
  }


}

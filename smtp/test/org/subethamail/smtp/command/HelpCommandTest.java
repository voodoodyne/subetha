package org.subethamail.smtp.command;

import org.subethamail.smtp.command.CommandTestCase;
import org.subethamail.smtp.command.DummySMTPServerContext;
import org.subethamail.smtp.command.HelpCommand;
import org.subethamail.smtp.command.NoopCommand;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class HelpCommandTest extends CommandTestCase {
  private HelpCommand helpCommand;

  public void testHelpCommandWithoutArgList() throws Exception {
    new NoopCommand(commandDispatcher);
    assertEquals(HelpMessageTest.NOOP_HELP_OUTPUT, helpCommand.execute("help noop", session));
  }

  public void testHelpCommandWithArgList() throws Exception {
    assertEquals(HelpMessageTest.HELP_HELP_OUTPUT, helpCommand.execute("help help", session));
  }

  public void testUnknownTopic() throws Exception {
    assertEquals("504 HELP topic \"foo\" unknown.", helpCommand.execute("HELP foo", session));
  }

  public void testHelp() throws Exception {
    commandDispatcher.setServerContext(new DummySMTPServerContext("1.0a2", "example.subetha.org", 25));

    String expectedOutput = "214-This is the SubEtha SMTP Server version 1.0a2 running on example.subetha.org\n" +
        "214-Topics:\n" +
        "214-    HELP\n" +
        "214-For more info use \"HELP <topic>\".\n" +
        "214-For more information about this server, visit:\n" +
        "214-    http://subetha.tigris.org\n" +
        "214-To report bugs in the implementation, send email to:\n" +
        "214-    issues@subetha.tigris.org\n" +
        "214-For local information send email to Postmaster at your site.\n" +
        "214 End of HELP info";
    assertEquals(expectedOutput, commandDispatcher.executeCommand("HELP", session));
  }

  public void testHelpDSN() throws Exception {
    // TODO(imf): Implement
  }

  public void testHelpTopicListing() throws Exception {
    assertEquals("214-    HELP\n", helpCommand.getFormattedTopicList());
    new NoopCommand(commandDispatcher);
    assertEquals("214-    HELP    NOOP\n", helpCommand.getFormattedTopicList());
  }

  protected void setUp() throws Exception {
  super.setUp();
    helpCommand = new HelpCommand(commandDispatcher);
  }
}

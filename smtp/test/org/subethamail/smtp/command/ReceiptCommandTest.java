package org.subethamail.smtp.command;

import org.subethamail.smtp.command.CommandTestCase;
import org.subethamail.smtp.command.DummyMessageListener;
import org.subethamail.smtp.command.DummySMTPServerContext;
import org.subethamail.smtp.command.ReceiptCommand;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class ReceiptCommandTest extends CommandTestCase {

  public void testMailCommand() throws Exception {
    final DummySMTPServerContext dummyServerContext = new DummySMTPServerContext("1.0a2", "example.subetha.org", 25);
    dummyServerContext.addRecipientHost("subetha.org");
    dummyServerContext.register(new DummyMessageListener());
    commandDispatcher.setServerContext(dummyServerContext);

    assertNull(session.getSender());
    assertEquals("503 Need MAIL before RCPT.", commandDispatcher.executeCommand("RCPT TO: test@subetha.org", session));
    session.setSender("test@example.com");
    assertEquals("501 Syntax: RCPT TO: <address>  Error in parameters: \"\"", commandDispatcher.executeCommand("RCPT", session));
    assertEquals("550 <test@otherdomain.org> Relaying denied.",
        commandDispatcher.executeCommand("RCPT TO: test@otherdomain.org", session));
    assertEquals(0, session.getRecipients().size());
    assertEquals("553 <test@subetha.org> User or list unknown.",
        commandDispatcher.executeCommand("RCPT TO: test@subetha.org", session));
    assertEquals(0, session.getRecipients().size());
    assertEquals("250 <validuser@subetha.org> Recipient ok.",
        commandDispatcher.executeCommand("RCPT TO: validuser@subetha.org", session));
    assertEquals(1, session.getRecipients().size());
  }

  public void testReceiptCommandHelp() throws Exception {
    assertEquals("214-RCPT TO: <recipient> [ <parameters> ]\n" +
        "214-    Specifies the recipient. Can be used any number of times.\n" +
        "214-    Parameters are ESMTP extensions. See \"HELP DSN\" for details.\n" +
        "214 End of RCPT info", commandDispatcher.getHelpMessage("RCPT").toOutputString());
  }

  protected void setUp() throws Exception {
    super.setUp();
    new ReceiptCommand(commandDispatcher);
  }

}

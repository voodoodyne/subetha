package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class ReceiptCommand extends BaseCommand {
  // TODO(imf): Split into SMTP and ESMTP versions.
  public ReceiptCommand(CommandDispatcher commandDispatcher) {
    super(commandDispatcher, "RCPT");
    helpMessage = new HelpMessage("RCPT", "TO: <recipient> [ <parameters> ]",
        "Specifies the recipient. Can be used any number of times.\n" +
        "Parameters are ESMTP extensions. See \"HELP DSN\" for details.");
  }

  @Override
  public String execute(String commandString, Session session) {
    String args = getArgPredicate(commandString);
    if (session.getSender() == null) {
      return "503 Need MAIL before RCPT.";
    } else if (! args.toUpperCase().startsWith("TO:")) {
      return "501 Syntax: RCPT TO: <address>  Error in parameters: \"" + getArgPredicate(commandString) + "\"";
    } else {
      final String recipientAddress = extractEmailAddress(args, 3);
      if (session.isRecipientDomainFilteringEnabled() && ! canAcceptMailToDomain(recipientAddress)) {
        return "550 <" + recipientAddress + "> Relaying denied.";
      }
      if (isValidRecipient(recipientAddress, session)) {
        session.addRecipient(recipientAddress);
        return ("250 <" + recipientAddress + "> Recipient ok.");
      } else {
        return "553 <" + recipientAddress + "> User or list unknown.";
      }
    }
  }

  private boolean isValidRecipient(String recipientAddress, Session session) {
    return commandDispatcher.getServerContext().accept(session.getSender(), recipientAddress);
  }

  private boolean canAcceptMailToDomain(String recipientAddress) {
    return commandDispatcher.getServerContext().getValidRecipientHosts()
        .contains(getDomain(recipientAddress));
  }

  protected String getDomain(String address) {
    // TODO(imf): Make bounds safe.
    int index = address.indexOf("@");
    return address.substring(index + 1).trim();
  }
}

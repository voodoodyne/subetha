package org.subethamail.smtp.session;

import org.subethamail.smtp.session.Session;
import org.subethamail.smtp.i.SMTPServerContext;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DummySession extends Session {
  private String messageId;

  public DummySession(String remoteHostname, SMTPServerContext serverContext) {
    super(serverContext, remoteHostname);
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String generateMessageId() {
    return messageId;
  }
}

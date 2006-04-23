package org.subethamail.smtp.session;

import org.subethamail.smtp.session.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DummySession extends Session {
  private String messageId;

  public DummySession(String remoteHostname) {
    super(remoteHostname);
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String generateMessageId() {
    return messageId;
  }
}

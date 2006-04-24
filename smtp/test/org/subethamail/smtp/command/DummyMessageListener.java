package org.subethamail.smtp.command;

import org.subethamail.smtp.i.MessageListener;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DummyMessageListener implements MessageListener {
  private String message;

  public boolean accept(String from, String recipient) {
    if (recipient.equals("validuser@subethamail.org")) return true;
    return false;
  }

  public void deliver(String from, String recipient, byte[] data) {
    this.message = new String(data);
  }

  public String getMessage() {
    return message;
  }
}

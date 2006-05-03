package org.subethamail.smtp.command;

import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.TooMuchDataException;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DummyMessageListener implements MessageListener {
  private String message;

  public boolean accept(String from, String recipient) {
    if (recipient.equals("validuser@subethamail.org")) return true;
    return false;
  }

  public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
    throw new TooMuchDataException("I can't take any data yet.");
  }

//  public void deliver(String from, String recipient, byte[] data) {
//    this.message = new String(data);
//  }

  public String getMessage() {
    return message;
  }
}

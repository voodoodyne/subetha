package org.subethamail.smtp.command;

import org.subethamail.smtp.i.MessageListener;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.subethamail.smtp.i.SMTPServerContext;
import org.subethamail.smtp.server.ServerRejectedException;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class DummySMTPServerContext implements SMTPServerContext {
  private final String serverVersion;
  private final String hostname;
  private int port;
  private List<String> validRecipientHosts; // TODO(imf): Rename this variable to something more standard and descriptive.
  private List<MessageListener> listeners;
  private CommandDispatcher commandDispatcher;

  public DummySMTPServerContext(String serverVersion, String hostname, int port) {
    this.serverVersion = serverVersion;
    this.hostname = hostname;
    this.port = port;
    validRecipientHosts = new ArrayList<String>();
    listeners = new ArrayList<MessageListener>();
  }

  public DummySMTPServerContext() {
    serverVersion = "test";
    hostname = "test.subetha.org";
    validRecipientHosts = new ArrayList<String>();
    listeners = new ArrayList<MessageListener>();
  }

  public void addRecipientHost(String hostname) {
    validRecipientHosts.add(hostname);
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public String getHostname() {
    return hostname;
  }

  public List<String> getValidRecipientHosts() {
    return validRecipientHosts;
  }

  public void start() {
    // NOOP.
  }

  public void stop() {
    // NOOP.
  }

  public String resolveHost(String hostname) throws IOException, ServerRejectedException {
    if (hostname.equalsIgnoreCase("remotehost.example.com")) return "remotehost.example.com/192.0.2.1";
    if (hostname.equalsIgnoreCase("test.subetha.org")) return "test.subetha.org/192.0.3.1";
    if (hostname.equalsIgnoreCase("spambox.blackhat.org")) {
      throw new ServerRejectedException("Traffic from your server denied access.");
    }
    throw new IOException("Could not resolve hostname");
  }

  public CommandDispatcher getCommandDispatcher() {
    return commandDispatcher;
  }

  public void register(MessageListener listener) {
    listeners.add(listener);
  }

  public void deregister(MessageListener listener) {
    listeners.remove(listener);
  }

  public boolean accept(String from, String recipient) {
    for (MessageListener messageListener : listeners) {
      if (messageListener.accept(from, recipient)) return true;
    } // else
    return false;
  }

  public void deliver(String from, String recipient, byte[] data) {
    for (MessageListener messageListener : listeners) {
      if (messageListener.accept(from, recipient)) {
        messageListener.deliver(from, recipient, data);
      }
    }
  }

  public int getPort() {
    return port;
  }

  public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }
}

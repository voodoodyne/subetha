package org.subethamail.smtp.session;

import org.subethamail.smtp.i.SMTPServerContext;

import java.util.List;
import java.util.ArrayList;
import java.net.Socket;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class Session {
  private boolean verbose = false;
  private boolean esmtp = false;
  private String remoteHostname = "";
  private String sender = null;
  private List<String> recipients = new ArrayList<String>();
  private boolean active = true;
  private String declaredRemoteHostname;
  private Socket socket;
  private boolean dataMode = false;
  private List<String> messageLines = new ArrayList<String>();
  private SMTPServerContext serverContext;

  public Session(SMTPServerContext serverContext, String remoteHostname) {
    this.serverContext = serverContext;
    this.remoteHostname = remoteHostname;
  }

  // TODO(imf): Should probably actually pass the SocketHandler instead.
  public Session(final SMTPServerContext serverContext, final Socket socket) {
    this.serverContext = serverContext;
    this.socket = socket;
    remoteHostname = socket.getInetAddress().getCanonicalHostName();
  }

  public void reset() {
    verbose = false;
    esmtp = false;
    sender = null;
    recipients = null;
    messageLines.clear();
    dataMode = false;
  }

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public boolean isEsmtp() {
    return esmtp;
  }

  public void setEsmtp(boolean esmtp) {
    this.esmtp = esmtp;
  }

  public String getRemoteHostname() {
    return remoteHostname;
  }

  public void setRemoteHostname(String remoteHostname) {
    this.remoteHostname = remoteHostname;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public List<String> getRecipients() {
    return recipients;
  }

  public void addRecipient(String recipientAddress) {
    recipients.add(recipientAddress);
  }

  public boolean isActive() {
    return active;
  }

  public void quit() {
    active = false;
  }

  public String getDeclaredRemoteHostname() {
    return declaredRemoteHostname;
  }

  public void setDeclaredRemoteHostname(String declaredRemoteHostname) {
    this.declaredRemoteHostname = declaredRemoteHostname;
  }

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public void setDataMode(boolean dataMode) {
    this.dataMode = dataMode;
  }

  public boolean isDataMode() {
    return dataMode;
  }

  public SMTPServerContext getServerContext() {
    return serverContext;
  }

  public String generateMessageId() {
    // TODO(imf): Implement a message ID generator here.
    return null;
  }

  public String getMessage() {
    StringBuilder stringBuilder = new StringBuilder();
    for (String line : messageLines) {
      stringBuilder.append(line);
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }

  public void addData(String line) {
    messageLines.add(line);
  }

  public void flush(SMTPServerContext SMTPServerContext) {
    for (String recipient : recipients) {
      SMTPServerContext.deliver(sender, recipient, getMessage().getBytes());
    }
    reset();
  }

  public boolean isRecipientDomainFilteringEnabled() {
    return serverContext.getRecipientDomainFilteringEnabled();
  }
}

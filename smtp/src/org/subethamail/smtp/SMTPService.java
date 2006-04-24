/*
 * $Id$
 * $URL$
 */

package org.subethamail.smtp;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.MessageListenerRegistry;
import org.subethamail.smtp.i.SMTPServerContext;
import org.subethamail.smtp.server.ServerRejectedException;
import org.subethamail.smtp.server.SMTPServiceCore;
import org.subethamail.smtp.command.CommandDispatcher;

/**
 * @author Jeff Schnitzer
 */
@Service(name="SMTPService", objectName="subetha:service=SMTP")
@SecurityDomain("subetha")
@RolesAllowed("siteAdmin")
public class SMTPService implements MessageListenerRegistry, SMTPManagement, SMTPServerContext
{
  /** */
  private static Log log = LogFactory.getLog(SMTPService.class);

  /**
   * There is no ConcurrentHashSet, so we make up our own by mapping the
   * object to itself.
   */
  Map<MessageListener, MessageListener> listeners = new ConcurrentHashMap<MessageListener, MessageListener>();
  private int port;
  private String hostname;
  private List<String> validRecipientHosts = new ArrayList<String>();
  private boolean hostResolutionEnabled = true;
  private SMTPServiceCore serviceCore;
  private CommandDispatcher commandDispatcher;
  private boolean recipientDomainFilteringEnabled = false;

  public SMTPService() {
    try {
      hostname = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      hostname = "localhost";
    }
    port = 2500;
  }

  /**
   * @see MessageListenerRegistry#register(MessageListener)
   */
  public void register(MessageListener listener)
  {
    if (log.isInfoEnabled())
      log.info("Registering " + listener);

    this.listeners.put(listener, listener);
  }

  /**
   * @see MessageListenerRegistry#deregister(MessageListener)
   */
  public void deregister(MessageListener listener)
  {
    if (log.isInfoEnabled())
      log.info("De-registering " + listener);

    this.listeners.remove(listener);
  }

  public String getServerVersion() {
    return "1.0a2";
  }


  /**
   * @see org.subethamail.smtp.SMTPManagement#getHostname()
   * @return hostname
   */
  @PermitAll
  public String getHostname() {
    return hostname;
  }

  @PermitAll
  public void setHostResolutionEnabled(boolean state) {
    hostResolutionEnabled = state;
  }

  @PermitAll
  public boolean getHostResolutionEnabled() {
    return hostResolutionEnabled;
  }

  public List<String> getValidRecipientHosts() {
    return validRecipientHosts;
  }

  /**
   * @see SMTPManagement#start()
   */
  @PermitAll
  public void start() throws IOException, ServerRejectedException
  {
    log.info("Starting SMTP service");
    if (serviceCore == null) {
      serviceCore = new SMTPServiceCore(this);
    }
    serviceCore.start();
  }

  /**
   * @see SMTPManagement#stop()
   */
  @PermitAll
  public void stop()
  {
    log.info("Stopping SMTP service");
    serviceCore.stop();
  }

  /**
   * @see SMTPManagement#setPort
   * @param port
   */
  @PermitAll
  public void setPort(int port) {
    this.port = port;
  }

  @PermitAll
  public int getPort() {
    return port;
  }

  /**
   * see SMTPManagement#setHostname
   * @param hostname
   */
  @PermitAll
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String resolveHost(String hostname) throws IOException, ServerRejectedException {
    if (hostResolutionEnabled) {
      return hostname.trim() + "/" + InetAddress.getByName(hostname).getHostAddress();
    } else {
      return hostname;
    }
  }

  public CommandDispatcher getCommandDispatcher() {
    return commandDispatcher;
  }

  public boolean accept(String from, String recipient) {
    for (MessageListener messageListener : listeners.keySet()) {
      if (messageListener.accept(from, recipient)) return true;
    } // else
    return false;
  }

  public void deliver(String from, String recipient, byte[] data) {
    for (MessageListener messageListener : listeners.keySet()) {
      if (messageListener.accept(from, recipient)) {
        messageListener.deliver(from, recipient, data);
      }
    }
  }

  public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  @PermitAll
  public void setRecipientDomainFilteringEnabled(boolean recipientDomainFilteringEnabled) {
    this.recipientDomainFilteringEnabled = recipientDomainFilteringEnabled;
  }

  @PermitAll
  public boolean getRecipientDomainFilteringEnabled() {
    return recipientDomainFilteringEnabled;
  }

}

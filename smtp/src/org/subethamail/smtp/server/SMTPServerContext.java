package org.subethamail.smtp.server;

import java.io.IOException;
import java.util.List;

import org.subethamail.smtp.command.CommandDispatcher;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.i.MessageListenerRegistry;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public interface SMTPServerContext extends MessageListener, MessageListenerRegistry {

  public String getServerVersion();
  public String getHostname();
  public int getPort();
  public List<String> getValidRecipientHosts();
  public void start() throws IOException, ServerRejectedException;
  public void stop();
  public String resolveHost(String hostname) throws IOException, ServerRejectedException;
  public void setCommandDispatcher(CommandDispatcher commandDispatcher);
  public CommandDispatcher getCommandDispatcher();
  public void setRecipientDomainFilteringEnabled(boolean state);
  public boolean getRecipientDomainFilteringEnabled();

}

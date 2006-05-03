package org.subethamail.smtp.server;

import org.subethamail.smtp.i.MessageListenerRegistry;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.command.CommandDispatcher;

import java.util.List;
import java.io.IOException;

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

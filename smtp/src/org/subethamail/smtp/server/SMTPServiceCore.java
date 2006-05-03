package org.subethamail.smtp.server;

import org.subethamail.smtp.command.CommandDispatcher;
import org.subethamail.smtp.command.HelloCommand;
import org.subethamail.smtp.command.MailCommand;
import org.subethamail.smtp.command.ReceiptCommand;
import org.subethamail.smtp.command.DataCommand;
import org.subethamail.smtp.command.ResetCommand;
import org.subethamail.smtp.command.NoopCommand;
import org.subethamail.smtp.command.QuitCommand;
import org.subethamail.smtp.command.HelpCommand;
import org.subethamail.smtp.command.VerifyCommand;
import org.subethamail.smtp.command.ExpnCommand;
import org.subethamail.smtp.command.VerboseCommand;
import org.subethamail.smtp.command.CommandLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.ServerSocket;
import java.io.IOException;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class SMTPServiceCore implements Runnable {
  private SMTPServerContext serverContext;
  private boolean go = false;
  ServerSocket serverSocket;
  private CommandDispatcher commandDispatcher;
  private static Log log = LogFactory.getLog(SMTPServiceCore.class);


  public SMTPServiceCore(SMTPServerContext context) {
    this.serverContext = context;
    initializeCommandDispatcher();
    serverContext.setCommandDispatcher(commandDispatcher);
  }

  private void initializeCommandDispatcher() {
    commandDispatcher = new CommandDispatcher(serverContext);
    new CommandLogger(new HelloCommand(commandDispatcher));
//    new EhloCommand(commandDispatcher);
    new CommandLogger(new MailCommand(commandDispatcher));
    new CommandLogger(new ReceiptCommand(commandDispatcher));
    new DataCommand(commandDispatcher);
    new ResetCommand(commandDispatcher);
    new NoopCommand(commandDispatcher);
    new CommandLogger(new QuitCommand(commandDispatcher));
    new HelpCommand(commandDispatcher);
    new VerifyCommand(commandDispatcher);
    new ExpnCommand(commandDispatcher);
    new VerboseCommand(commandDispatcher);
//    new EtrnCommand(commandDispatcher);
//    new DsnCommand(commandDispatcher);
  }


  public void start() throws IOException {
    go = true;
    serverSocket = new ServerSocket(serverContext.getPort());
    new Thread(this).start();
  }

  public void stop() {
    go = false;
    try {
      serverSocket.close();
    } catch (IOException e) {
      log.error("Failed to close server socket.", e);
    }
  }

  public void run() {
    log.info("SMTP Server socket started.");
    while (go) {
      try {
        new SocketHandler(serverContext, serverSocket.accept());
      }
      catch (IOException ioe) {
        if (go) {
          log.error("IOException accepting connection in SMTPServiceCore", ioe);
        } // Otherwise this is just the socket complaining that it was closed while in accept.
      }
    }
    try {
      serverSocket.close();
      log.info("SMTP Server socket shut down.");
    } catch (IOException e) {
      log.error("Failed to close server socket.", e);
    }

  }
}

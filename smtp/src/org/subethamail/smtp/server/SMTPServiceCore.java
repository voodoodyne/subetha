package org.subethamail.smtp.server;

import org.subethamail.smtp.i.SMTPServerContext;
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
    new HelloCommand(commandDispatcher);
//    new EhloCommand(commandDispatcher);
    new MailCommand(commandDispatcher);
    new ReceiptCommand(commandDispatcher);
    new DataCommand(commandDispatcher);
    new ResetCommand(commandDispatcher);
    new NoopCommand(commandDispatcher);
    new QuitCommand(commandDispatcher);
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
  }

  public void run() {
    while (go) {
      try {
        new SocketHandler(serverContext, serverSocket.accept());
      }
      catch (IOException ioe) {
        log.error("IOException accepting connection in SMTPServiceCore", ioe);
      }
    }
  }
}

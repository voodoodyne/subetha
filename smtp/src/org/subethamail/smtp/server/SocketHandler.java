package org.subethamail.smtp.server;

import org.subethamail.smtp.session.Session;

import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.subethamail.smtp.server.ServerRejectedException;
import org.subethamail.smtp.i.SMTPServerContext;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
class SocketHandler
{
  private SMTPServerContext serverContext;

  public SocketHandler(SMTPServerContext serverContext, Socket aSocket) throws IOException
  {
    this.serverContext = serverContext;
    Session session = new Session(serverContext, aSocket);
    PrintWriter out = new PrintWriter(aSocket.getOutputStream());
    BufferedReader in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
    try {
      out.println("220-" + serverContext.getHostname()  + " SubEthaMail SMTP Server " + serverContext.getServerVersion() + "; " + new Date());
      out.println("220 You are " + serverContext.resolveHost(session.getRemoteHostname()));
    } catch (ServerRejectedException e) {
      session.quit();
      out.println("221 " + serverContext.getHostname() + " closing connection. " + e.getMessage());
    }
    out.flush();
    String command;
    while (session.isActive()) {
      command = (in.readLine()).trim();
      out.println(serverContext.getCommandDispatcher().executeCommand(command, session));
      out.flush();
    }
    in.close();
    out.close();
    aSocket.close();
  }
}

package org.subethamail.smtp.server;

import org.subethamail.smtp.server.SMTPServer;

import java.io.IOException;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
public class StandAloneLauncher {
  public static void main(String[] args) throws IOException {
    SMTPServer server = new SMTPServer(args[0], Integer.parseInt(args[1]));
    System.out.println("Starting SubEtha SMTPServer on port " + server.getPort());
    server.start();
  }
}

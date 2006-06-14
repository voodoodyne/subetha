package org.subethamail.core.postfix;

import java.io.IOException;
import java.net.Socket;

/**
 * This context is used for managing information
 * about a connection.
 * 
 * @author Jon Stevens
 */
public interface ConnectionContext
{
	public ConnectionHandler getConnection();
	public TcpTableServer getServer();
	public Socket getSocket();
	public void sendResponse(String response) throws IOException;
}

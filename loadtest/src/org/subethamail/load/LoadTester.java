/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.smtp.MessageListener;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Normal main() class that starts a simple load test receiver.
 * 
 * @author Jeff Schnitzer
 */
public class LoadTester
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(LoadTester.class);
	
	/** */
	CountingListener listener = new CountingListener();
	SMTPServer server;
	
	/** */
	public LoadTester() throws Exception
	{
		Collection<MessageListener> listeners = new ArrayList<MessageListener>(1);
		listeners.add(this.listener);
		
		this.server = new SMTPServer(listeners);
		this.server.setPort(2525);
		this.server.setHostName("localhost");
	}
	
	/** */
	public void start()
	{
		this.server.start();
		
		int lastCount = this.listener.getTotalCount();
		long lastTime = System.currentTimeMillis();
		
		while (true)
		{
			
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException ex) { throw new RuntimeException(ex); }
			
			int nextCount = this.listener.getTotalCount();
			long nextTime = System.currentTimeMillis();
			
			int count = nextCount - lastCount;
			long duration = nextTime - lastTime;
			float perSecond = (float)(count*1000) / (float)duration;
			
			System.out.println("Received " + nextCount + " messages, " + perSecond + " messages/second");
			
			lastCount = nextCount;
			lastTime = nextTime;
		}
	}
	
	/** */
	public static void main(String[] args) throws Exception
	{
		LoadTester tester = new LoadTester();
		tester.start();
	}
}

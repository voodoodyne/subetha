/*
 * $Id$
 * $URL$
 */

package org.subethamail.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Normal main() class that starts a simple load test receiver.
 * 
 * @author Jeff Schnitzer
 */
public class LoadTester implements Runnable
{
	/** */
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoadTester.class);
	
	/** */
	CountingListener listener = new CountingListener();
	SMTPServer server;
	
	/** */
	public LoadTester(String host, int port) throws Exception
	{
		this.server = new SMTPServer(new SimpleMessageListenerAdapter(this.listener));
		this.server.setPort(port);
		this.server.setHostName(host);
	}
	
	/** */
	@Override
	public void run()
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
		LoadTester tester = new LoadTester("localhost",2525);
		tester.run();
	}
}

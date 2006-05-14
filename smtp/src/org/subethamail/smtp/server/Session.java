package org.subethamail.smtp.server;

import java.util.ArrayList;
import java.util.List;
import org.subethamail.smtp.i.MessageListener;

/**
 * A sesssion describes events which happen during a
 * SMTP session.
 * 
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
@SuppressWarnings("serial")
public class Session
{
	private boolean dataMode = false;
	private boolean hasSeenHelo = false;
	private boolean active = true;
	private String sender = null;
	private List<Delivery> deliveries = new ArrayList<Delivery>();

	public Session()
	{
	}

	public class Delivery
	{
		MessageListener listener;
		String recipient;

		public Delivery(MessageListener listener, String recipient)
		{
			this.listener = listener;
			this.recipient = recipient;
		}

		public MessageListener getListener()
		{
			return listener;
		}

		public String getRecipient()
		{
			return recipient;
		}
	}

	public boolean isActive()
	{
		return active;
	}

	public void quit()
	{
		active = false;
	}

	public void addListener(MessageListener messageListener, String recipient)
	{
		Delivery delivery = new Delivery(messageListener, recipient);
		deliveries.add(delivery);
	}

	public List<Delivery> getDeliveries()
	{
		return deliveries;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public boolean hasSeenHelo()
	{
		return hasSeenHelo;
	}

	public void setHasSeenHelo(boolean hasSeenHelo)
	{
		this.hasSeenHelo = hasSeenHelo;
	}

	public boolean isDataMode()
	{
		return this.dataMode;
	}

	public void setDataMode(boolean dataMode)
	{
		this.dataMode = dataMode;
	}

	public void reset()
	{
		this.sender = null;
		this.dataMode = false;
		this.active = true;
		this.hasSeenHelo = false;
		this.deliveries.clear();
	}
}

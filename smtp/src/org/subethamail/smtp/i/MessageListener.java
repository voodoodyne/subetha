/*
 * $Id$
 * $URL$
 */

package org.subethamail.smtp.i;

/**
 * Objects which want access to messages received with SMTP should
 * implement this interface and register themselves with the
 * SMTPListenerRegistry.
 * 
 * While the SMTP message is being received, all listeners are
 * asked if they want to accept each recipient.  After the message
 * has arrived, the message is handed off to all accepting listeners.
 * 
 * @author Jeff Schnitzer
 */
public interface MessageListener
{
	/** 
	 * Called once for every RCPT TO during a SMTP exchange.
	 * 
	 * @param from is a rfc822-compliant email address.
	 * @param recipient is a rfc822-compliant email address.
	 * 
	 * @return true if the listener wants delivery of the message,
	 *  false if the message is not for this listener.
	 */
	public boolean accept(String from, String recipient);
	
	/** 
	 * When a message arrives, this method will be called once for
	 * every recipient this listener accepted.
	 * 
	 * @param from is the envelope sender in rfc822 form
	 * @param recipient will be an accepted recipient in rfc822 form
	 */
	public void deliver(String from, String recipient, byte[] data);
  // TODO(imf): Deliver will probably need to handle streams instead
  // of byte arrays, so larger content can be handled.
  // That, or it will need to be able to be called more than once for
  // a single message.
}

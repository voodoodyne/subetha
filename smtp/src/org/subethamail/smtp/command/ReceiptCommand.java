package org.subethamail.smtp.command;

import java.io.IOException;
import org.subethamail.smtp.i.MessageListener;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.ConnectionContext;
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.smtp.server.Session;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
public class ReceiptCommand extends BaseCommand
{
	// TODO(imf): Split into SMTP and ESMTP versions.
	public ReceiptCommand()
	{
		super("RCPT",
				"Specifies the recipient. Can be used any number of times.\n"
						+ "Parameters are ESMTP extensions. See \"HELP DSN\" for details.",
				"TO: <recipient> [ <parameters> ]");
	}

	@Override
	public void execute(String commandString, ConnectionContext context) throws IOException
	{
		Session session = context.getSession();
		if (session.getSender() == null)
		{
			context.sendResponse("503 Need MAIL before RCPT.");
			return;
		}

		String args = getArgPredicate(commandString);
		if (!args.toUpperCase().startsWith("TO:"))
		{
			context.sendResponse(
					"501 Syntax: RCPT TO: <address>  Error in parameters: \""
					+ args + "\"");
			return;
		}
		else
		{
			String recipientAddress = extractEmailAddress(args, 3);
			if (handleRecipient(recipientAddress, context))
			{
				context.sendResponse("250 Ok");
			}
			else
			{
				context.sendResponse("553 <" + recipientAddress + "> address unknown.");
			}
		}
	}

	private boolean handleRecipient(String recipientAddress, ConnectionContext context)
	{
		Session session = context.getSession();
		boolean addedListener = false;

		for (MessageListener listener : ((SMTPServer)context.getServer()).getListeners().values())
		{
			if (listener.accept(session.getSender(), recipientAddress))
			{
				session.addListener(listener, recipientAddress);
				addedListener = true;
			}
		}

		return addedListener;
	}
}

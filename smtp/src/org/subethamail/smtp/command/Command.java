package org.subethamail.smtp.command;

import org.subethamail.smtp.session.Session;

/**
 * Created by IntelliJ IDEA.
 * User: imf
 * Date: Apr 24, 2006
 * Time: 3:06:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Command {
    public String execute(String commandString, Session session);

    public HelpMessage getHelp();

    public String getName();

    void replaceWith(Command command);
}

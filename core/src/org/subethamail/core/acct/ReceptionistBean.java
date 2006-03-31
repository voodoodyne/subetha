/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.acct;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.EJB;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.security.SecurityDomain;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.Receptionist;
import org.subethamail.core.acct.i.ReceptionistRemote;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.post.PostOffice;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the Receptionist interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="Receptionist")
@SecurityDomain("subetha")
@PermitAll
@RunAs("siteAdmin")
public class ReceptionistBean implements Receptionist, ReceptionistRemote
{
	/** */
	private static Log log = LogFactory.getLog(ReceptionistBean.class);

	/**
	 * The set of characters from which randomly generated
	 * passwords will be obtained.
	 */
	protected static final String PASSWORD_GEN_CHARS =
		"abcdefghijklmnopqrstuvwxyz" +
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"0123456789";
	
	/**
	 * The length of randomly generated passwords.
	 */
	protected static final int PASSWORD_GEN_LENGTH = 6;
	
	/**
	 * A known prefix so we know if decryption worked properly
	 */
	private static final String SUBSCRIBE_TOKEN_PREFIX = "sub";
	
	/** */
	@EJB DAO dao;
	@EJB PostOffice postOffice;
	@EJB Encryptor encryptor;
	
	/**
	 * For generating random passwords.
	 */
	protected Random randomizer = new Random();
	
	/**
	 * @see Receptionist#requestSubscription(String, Long, String)
	 * 
	 * The token emailed is encrypted "email:listId:name", where
	 * all the elements have been URLEncoded first.
	 */
	public void requestSubscription(String email, Long listId, String name) throws NotFoundException, MessagingException
	{
		if (log.isDebugEnabled())
			log.debug("Requesting to subscribe " + email + " to list " + listId);
		
		MailingList mailingList = this.dao.findMailingList(listId);
		
		List<String> plainList = new ArrayList<String>();
		plainList.add(SUBSCRIBE_TOKEN_PREFIX);
		plainList.add(email);
		plainList.add(listId.toString());
		plainList.add(name);
		
		String cipherText = this.encryptor.encryptList(plainList);
		
		this.postOffice.sendSubscribeToken(mailingList, email, cipherText);
	}
	
	/**
	 * @see Receptionist#subscribe(String)
	 */
	public Long subscribe(String cipherToken) throws BadTokenException
	{
		List<String> plainList = this.encryptor.decryptList(cipherToken);
		
		if (plainList.isEmpty() || !plainList.get(0).equals(SUBSCRIBE_TOKEN_PREFIX))
			throw new BadTokenException("Invalid token");
		
		String email = plainList.get(1);
		Long listId = Long.valueOf(plainList.get(2));
		String name = plainList.get(3);
		
		//TODO
		
		return null;
	}

	/**
	 * @see Receptionist#forgotPassword(String)
	 */
	public void forgotPassword(String email) throws NotFoundException, MessagingException
	{
		EmailAddress addy = this.dao.findEmailAddress(email);
		
		// TODO
		this.postOffice.sendPassword(null, null);
	}
	
	/**
	 * @return a valid password.
	 */
	protected String generateRandomPassword()
	{
		StringBuffer gen = new StringBuffer(PASSWORD_GEN_LENGTH);
		
		for (int i=0; i<PASSWORD_GEN_LENGTH; i++)
		{
			int which = (int)(PASSWORD_GEN_CHARS.length() * randomizer.nextDouble());
			
			gen.append(PASSWORD_GEN_CHARS.charAt(which));
		}
		
		return gen.toString();
	}
}

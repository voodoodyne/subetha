/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.acct;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.security.auth.login.FailedLoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.BadTokenException;
import org.subethamail.core.acct.i.Receptionist;
import org.subethamail.core.acct.i.ReceptionistRemote;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.util.CipherUtil;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.dao.DAO;

/**
 * Implementation of the Receptionist interface.
 * 
 * @author Jeff Schnitzer
 */
@Stateless(name="Receptionist")
//@SecurityDomain("subetha")
//@RunAs("god")
public class ReceptionistEJB implements Receptionist, ReceptionistRemote
{
	/** */
	private static Log log = LogFactory.getLog(ReceptionistEJB.class);

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
	 * The key used to encrypt (and decrypt) the "subscribe" token.
	 * TODO:  put this on a rotation schedule
	 */
	private static final byte[] SUBSCRIBE_TOKEN_KEY = 
		{ 11, 39, 49, 55, 9, 75, 67, 13, 48, 119, 102, 21, 53, 80, 2, 9 };
	
	/**
	 * A known prefix so we know if decryption worked properly
	 */
	private static final String SUBSCRIBE_TOKEN_PREFIX = "sub";
	
	/** */
	@EJB DAO dao;
	@EJB PostOffice postOffice;
	
	/**
	 * For generating random passwords.
	 */
	protected java.util.Random randomizer = new java.util.Random();
	
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
		
		try
		{
			String cipherText = CipherUtil.encryptList(plainList, SUBSCRIBE_TOKEN_KEY);
			
			this.postOffice.sendSubscribeToken(mailingList, email, cipherText);
		}
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }	// should be impossible
	}
	
	/**
	 * @see Receptionist#subscribe(String)
	 */
	public Long subscribe(String cipherToken) throws BadTokenException
	{
		List<String> plainList;
		try
		{
			plainList = CipherUtil.decryptList(cipherToken, SUBSCRIBE_TOKEN_KEY);
		}
		catch (GeneralSecurityException ex) { throw new BadTokenException(ex); }
		
		if (plainList.isEmpty() || !plainList.get(0).equals(SUBSCRIBE_TOKEN_PREFIX))
			throw new BadTokenException("Invalid token");
		
		String email = plainList.get(1);
		Long listId = Long.valueOf(plainList.get(2));
		String name = plainList.get(3);
		
		//TODO
		
		return null;
	}

	/**
	 * @see Receptionist#authenticate(String, String)
	 */
	public Long authenticate(String email, String password) throws NotFoundException, FailedLoginException
	{
		if (log.isDebugEnabled())
			log.debug("Authenticating " + email);
		
		EmailAddress addy = this.dao.findEmailAddress(email);
		
		if (!addy.getPerson().checkPassword(password))
			throw new FailedLoginException("Bad password.");
		
		return addy.getPerson().getId();
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

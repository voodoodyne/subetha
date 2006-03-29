/*
 * $Id: ReceptionistEJB.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/ReceptionistEJB.java $
 */

package org.subethamail.core.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.annotation.EJB;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Depends;
import org.jboss.annotation.ejb.Service;
import org.jboss.util.Base64;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.entity.Config;
import org.subethamail.entity.dao.DAO;

/**
 * Performs encryption and decryption using a constant key.  The
 * key is set as a config value.  If the key doesn't exist when
 * the service is started, a random key is generated.
 * 
 * Note that this bean does NOT have a remote interface.
 * 
 * @author Jeff Schnitzer
 */
@Service(name="Encryptor", objectName="subetha:service=Encryptor")
// This depends annotation can be removed when JBoss fixes dependency bug.
@Depends("jboss.j2ee:ear=subetha.ear,jar=entity.jar,name=DAO,service=EJB3")
public class EncryptorService implements Encryptor, EncryptorManagement
{
	/** */
	private static Log log = LogFactory.getLog(EncryptorService.class);
	
	/**
	 * The name of the config value that holds the current encryption key.
	 * The actual config value will be a base64-encoded string. 
	 */
	private static final String KEY_CONFIG_ID = "cipherKey";
	
	/**
	 * The number of bytes in a key.
	 */
	private static final int KEY_LENGTH = 16;
	
	
	/**
	 * An initial vector for encoding.  The content is more or less irrelevant.
	 */
	private static final byte[] IV = new byte[KEY_LENGTH];
	static
	{
		for (int i=0; i<KEY_LENGTH; i++)
			IV[i] = (byte)(i+10);
	}

	/** */
	@EJB DAO dao;

	/**
	 * @see EncryptorManagement#start()
	 */
	public void start() throws Exception
	{
		// If we don't already have a key, generate one
		try
		{
			Config cfg = this.dao.findConfig(KEY_CONFIG_ID);
			
			// Might as well sanity check it
			String value = (String)cfg.getValue();
			
			if (value == null || value.length() == 0)
				cfg.setValue(this.generateKey());
		}
		catch (NotFoundException ex)
		{
			Config cfg = new Config(KEY_CONFIG_ID, this.generateKey());
			this.dao.persist(cfg);
		}
	}
	
	/**
	 * Randomly generates a new, base64-encoded key.  The raw key
	 * will be 16 bytes long.
	 */
	String generateKey()
	{
		byte[] generated = new byte[KEY_LENGTH];

		Random rnd = new SecureRandom();
		rnd.nextBytes(generated);
		
		return Base64.encodeBytes(generated);
	}
	
	/**
	 * @return the current key from the config
	 */
	byte[] getKey()
	{
		String base64 = (String)this.dao.getConfigValue(KEY_CONFIG_ID);
		return Base64.decode(base64);
	}
	
	/**
	 * @see Encryptor#encrypt(String)
	 */
	public byte[] encrypt(String plainText)
	{
		if (log.isDebugEnabled())
			log.debug("Encrypting: " + plainText);
		
		try
		{
			byte[] plainBytes = plainText.getBytes("UTF-8");
			
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			byte[] cipherText = aes.doFinal(plainBytes);
				
			return cipherText;
		}
		catch (UnsupportedEncodingException ex) { throw new EJBException(ex); }
		catch (GeneralSecurityException ex) { throw new EJBException(ex); }
	}

	/**
	 * @see Encryptor#decrypt(byte[])
	 */
	public String decrypt(byte[] cipherText)
	{
		try
		{
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			byte[] plainText = aes.doFinal(cipherText);
			
			String result = new String(plainText, "UTF-8");
			
			if (log.isDebugEnabled())
				log.debug("Decrypted to: " + result);
			
			return result;
		}
		catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
		catch (GeneralSecurityException ex) { throw new EJBException(ex); }
	}

	/**
	 * @see Encryptor#encryptString(String)
	 */
	public String encryptString(String plainText)
	{
		byte[] cipherText = this.encrypt(plainText);
		
		return Base64.encodeBytes(cipherText);
	}

	/**
	 * @see Encryptor#decryptString(String)
	 */
	public String decryptString(String cipherText)
	{
		byte[] cipherBytes = Base64.decode(cipherText);
		
		return this.decrypt(cipherBytes);
	}

	/**
	 * @see Encryptor#decryptList(String)
	 */
	public String encryptList(List<String> parts)
	{
		StringBuffer buf = new StringBuffer(128);
		
		boolean first = true;
		
		for (String part: parts)
		{
			try
			{
				if (first)
					first = false;
				else
					buf.append(':');
				
				buf.append(URLEncoder.encode(part, "UTF-8"));
			}
			catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
		}
		
		return this.encryptString(buf.toString());
	}

	/**
	 * @see Encryptor#decryptList(String)
	 */
	public List<String> decryptList(String cipherText)
	{
		String plainString = this.decryptString(cipherText);
		
		StringTokenizer tok = new StringTokenizer(plainString, ":");
		
		List<String> result = new ArrayList<String>();
		
		while (tok.hasMoreTokens())
		{
			try
			{
				String part = URLDecoder.decode(tok.nextToken(), "UTF-8");
				result.add(part);
			}
			catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
		}
		
		return result;
	}
}

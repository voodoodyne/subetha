/*
 * $Id: EncryptorBean.java 988 2008-12-30 08:51:13Z lhoriman $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/admin/EncryptorBean.java $
 */

package org.subethamail.core.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.security.PermitAll;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJBException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ExpiredException;
import org.subethamail.core.util.EntityManipulatorBean;
import org.subethamail.entity.Config;

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
@SecurityDomain("subetha")
@PermitAll
public class EncryptorBean extends EntityManipulatorBean implements Encryptor, EncryptorManagement
{
	/** */
	private static Log log = LogFactory.getLog(EncryptorBean.class);
	
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

	/**
	 * @see EncryptorManagement#start()
	 */
	public void start() throws Exception
	{
		// If we don't already have a key, generate one
		try
		{
			Config cfg = this.em.get(Config.class, KEY_CONFIG_ID);
			
			// Might as well sanity check it
			String value = (String)cfg.getValue();
			
			if (value == null || value.length() == 0)
				cfg.setValue(this.generateKey());
		}
		catch (NotFoundException ex)
		{
			Config cfg = new Config(KEY_CONFIG_ID, this.generateKey());
			this.em.persist(cfg);
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
		
		return new String(Base64.encodeBase64(generated));
	}
	
	/**
	 * @return the current key from the config
	 */
	byte[] getKey()
	{
		String base64 = (String)this.em.findConfigValue(KEY_CONFIG_ID);
		return Base64.decodeBase64(base64.getBytes());
	}
	
	/**
	 * @see Encryptor#encrypt(byte[])
	 */
	public byte[] encrypt(byte[] plainText)
	{
		if (log.isDebugEnabled())
			log.debug("Encrypting " + plainText.length + " bytes");
		
		try
		{
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			return aes.doFinal(plainText);
		}
		catch (GeneralSecurityException ex) { throw new EJBException(ex); }
	}

	/**
	 * @see Encryptor#decrypt(byte[])
	 */
	public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException
	{
		try
		{
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			byte[] plainText = aes.doFinal(cipherText);
			
			if (log.isDebugEnabled())
				log.debug("Decrypted to: " + plainText.length + " bytes");
			
			return plainText;
		}
		catch (Exception ex) { throw new GeneralSecurityException(ex); }
	}

	/**
	 * @see Encryptor#encryptString(String)
	 */
	public byte[] encryptString(String plainText)
	{
		if (log.isDebugEnabled())
			log.debug("Encrypting: " + plainText);
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		try
		{
			// First write a timestamp
			long now = System.currentTimeMillis();
			out.writeLong(now);
			
			out.writeUTF(plainText);
			
			out.close();
		}
		catch (IOException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
		
		return this.encrypt(buf.toByteArray());
	}

	/**
	 * @see Encryptor#decryptString(byte[])
	 */
	public String decryptString(byte[] cipherText) throws GeneralSecurityException
	{
		return this.decryptString(cipherText, Long.MAX_VALUE);
	}
	
	/**
	 * @see Encryptor#decryptString(byte[], long)
	 */
	public String decryptString(byte[] cipherText, long maxAgeMillis) throws GeneralSecurityException, ExpiredException
	{
		byte[] plainText = this.decrypt(cipherText);
		
		ByteArrayInputStream inBuf = new ByteArrayInputStream(plainText);
		DataInputStream in = new DataInputStream(inBuf);

		try
		{
			long time = in.readLong();
			
			// First make sure token still good
			if ((System.currentTimeMillis() - time) > maxAgeMillis)
				throw new ExpiredException("Token expired");
	
			String result = in.readUTF();
			
			if (log.isDebugEnabled())
				log.debug("Decrypted to: " + result);
			
			return result;
		}
		catch (IOException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
	}
	

	/**
	 * @see Encryptor#encryptList(List)
	 */
	public byte[] encryptList(List<String> parts)
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		
		try
		{
			// First write a timestamp
			long now = System.currentTimeMillis();
			out.writeLong(now);
			
			for (String part: parts)
				out.writeUTF(part);
			
			out.close();
		}
		catch (IOException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
		
		return this.encrypt(buf.toByteArray());
	}

	/**
	 * @see Encryptor#decryptList(byte[])
	 */
	public List<String> decryptList(byte[] cipherText) throws GeneralSecurityException
	{
		return this.decryptList(cipherText, Long.MAX_VALUE);
	}
	
	/**
	 * @see Encryptor#decryptList(byte[], long)
	 */
	public List<String> decryptList(byte[] cipherText, long maxAgeMillis) throws GeneralSecurityException, ExpiredException
	{
		byte[] plainText = this.decrypt(cipherText);
		
		ByteArrayInputStream inBuf = new ByteArrayInputStream(plainText);
		DataInputStream in = new DataInputStream(inBuf);

		try
		{
			long time = in.readLong();
			
			// First make sure token still good
			if ((System.currentTimeMillis() - time) > maxAgeMillis)
				throw new ExpiredException("Token expired");
	
			List<String> result = new ArrayList<String>();
			
			while (in.available() > 0)
				result.add(in.readUTF());
			
			return result;
		}
		catch (IOException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
	}

}

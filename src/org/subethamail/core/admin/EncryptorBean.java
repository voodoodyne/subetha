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
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.java.Log;

import org.apache.commons.codec.binary.Base64;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.admin.i.Encryptor;
import org.subethamail.core.admin.i.ExpiredException;
import org.subethamail.core.util.SubEtha;
import org.subethamail.core.util.SubEthaEntityManager;
import org.subethamail.entity.Config;

/**
 * Performs encryption and decryption using a constant key.  The
 * key is set as a config value.  If the key doesn't exist when
 * the service is started, a random key is generated.
 * 
 * Note that this bean does NOT have a remote interface.
 * 
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Log
public class EncryptorBean implements Encryptor
{
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
	@Inject @SubEtha SubEthaEntityManager em;
	
	/* */
	@PostConstruct
	public void start() throws Exception
	{
	    log.log(Level.FINE,"Starting EncryptorBean, entitymanager is {0}", em);
		
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
		    log.log(Level.INFO,"Creating new cypher key for Encryptor, and storing it in the database");
			Config cfg = new Config(KEY_CONFIG_ID, this.generateKey());
			this.em.persist(cfg);
		}
	}
	
	/**
	 * Randomly generates a new, base64-encoded key.  The raw key
	 * will be 16 bytes long.
	 */
	protected String generateKey()
	{
		byte[] generated = new byte[KEY_LENGTH];

		Random rnd = new SecureRandom();
		rnd.nextBytes(generated);
		
		return new String(Base64.encodeBase64(generated));
	}
	
	/**
	 * @return the current key from the config
	 */
	protected byte[] getKey()
	{
		String base64 = (String)this.em.findConfigValue(KEY_CONFIG_ID);
		return Base64.decodeBase64(base64.getBytes());
	}
	
	/* */
	public byte[] encrypt(byte[] plainText)
	{
	    log.log(Level.FINE,"Encrypting {0} bytes", plainText.length);
		
		try
		{
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			return aes.doFinal(plainText);
		}
		catch (GeneralSecurityException ex) { throw new EJBException(ex); }
	}

	/* */
	public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException
	{
		try
		{
			SecretKey secretKey = new SecretKeySpec(this.getKey(), "AES");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
			
			byte[] plainText = aes.doFinal(cipherText);
			
			log.log(Level.FINE,"Decrypted to: {0} bytes", plainText.length);
			
			return plainText;
		}
		catch (Exception ex) { throw new GeneralSecurityException(ex); }
	}

	/* */
	public byte[] encryptString(String plainText)
	{
	    log.log(Level.FINE,"Encrypting: {0}", plainText);
		
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

	/* */
	public String decryptString(byte[] cipherText) throws GeneralSecurityException
	{
		return this.decryptString(cipherText, Long.MAX_VALUE);
	}
	
	/* */
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
			
			log.log(Level.FINE,"Decrypted to: {0}", result);
			
			return result;
		}
		catch (IOException ex)
		{
			// Should be impossible
			throw new RuntimeException(ex);
		}
	}
	

	/* */
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

	/* */
	public List<String> decryptList(byte[] cipherText) throws GeneralSecurityException
	{
		return this.decryptList(cipherText, Long.MAX_VALUE);
	}
	
	/* */
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

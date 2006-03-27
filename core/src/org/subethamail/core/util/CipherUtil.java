/*
 * $Id: BlornCipher.java 86 2006-02-22 03:36:01Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/util/BlornCipher.java $
 */

package org.subethamail.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.util.Base64;
import org.subethamail.core.acct.i.BadTokenException;


/**
 * Static methods to encrypt and decrypt strings
 *
 * @author Jeff Schnitzer
 */
public class CipherUtil
{
	/** */
	private static Log log = LogFactory.getLog(CipherUtil.class);
	
	/** Encrypt a string based on a key */
	public static String encrypt(String plainText, byte[] key) throws GeneralSecurityException
	{
		try
		{
			byte[] plainBytes = plainText.getBytes("UTF-8");
			
			SecretKey secretKey = new SecretKeySpec(key, "AES");
			byte[] iv = "0123452345676701".getBytes("ASCII");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			
			byte[] cipherText = aes.doFinal(plainBytes);
				
			String base64 = Base64.encodeBytes(cipherText);
			
			if (log.isDebugEnabled())
				log.debug("Encrypted " + plainText + " to " + base64);
			
			return base64;
		}
		catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
	}

	/** Decrypt a string based on a key */
	public static String decrypt(String cipherText, byte[] key) throws GeneralSecurityException
	{
		try
		{
			byte[] cipherBytes = Base64.decode(cipherText);
			
			SecretKey secretKey = new SecretKeySpec(key, "AES");
			byte[] iv = "0123452345676701".getBytes("ASCII");
			
			Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			
			byte[] plainText = aes.doFinal(cipherBytes);
			
			String result = new String(plainText, "UTF-8");
			
			if (log.isDebugEnabled())
				log.debug("Decrypted " + cipherText + " to " + result);
			
			return result;
		}
		catch (UnsupportedEncodingException ex) { throw new RuntimeException(ex); }
	}
	
	/**
	 * Takes a base64 encoded string and eliminates the '+' and '/'.
	 * Also eliminates any CRs.
	 * 
	 * Having tokens that are a seamless string of letters and numbers
	 * means that MUAs are less likely to linebreak a long token.
	 */
	public static String encodeBase62(String base64)
	{
		StringBuffer buf = new StringBuffer(base64.length() * 2);
		
		for (int i=0; i<base64.length(); i++)
		{
			char ch = base64.charAt(i);
			switch (ch)
			{
				case 'i':
					buf.append("ii");
					break;
					
				case '+':
					buf.append("ip");
					break;
					
				case '/':
					buf.append("is");
					break;
					
				case '=':
					buf.append("ie");
					break;
					
				case '\n':
					// Strip out
					break;
					
				default:
					buf.append(ch);
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("encodeBase62 from " + base64 + " to " + buf.toString());
		
		return buf.toString();
	}
	
	/**
	 * Returns a string encoded with encodeBase62 to its original
	 * (base64 encoded) state.
	 */
	public static String decodeBase62(String base62)
	{
		StringBuffer buf = new StringBuffer(base62.length());
		
		int i = 0;
		while (i < base62.length())
		{
			char ch = base62.charAt(i);
			
			if (ch == 'i')
			{
				i++;
				char code = base62.charAt(i);
				switch (code)
				{
					case 'i':
						buf.append('i');
						break;
						
					case 'p':
						buf.append('+');
						break;
						
					case 's':
						buf.append('/');
						break;
						
					case 'e':
						buf.append('=');
						break;
						
					default:
						throw new IllegalStateException("Illegal code in base62 encoding");
				}
			}
			else
			{
				buf.append(ch);
			}
			
			i++;
		}
		
		if (log.isDebugEnabled())
			log.debug("decodeBase62 from " + base62 + " to " + buf.toString());
		
		return buf.toString();
	}
	
	/**
	 * Fully encrypt a list of arbitrary Strings into a single base62 string
	 * 
	 * @param parts is the list of Strings to encrypt
	 * @param key is the AES encryption key to use
	 */
	public static String encryptList(List<String> parts, byte[] key) throws GeneralSecurityException
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
		
		String cipherToken = encrypt(buf.toString(), key);
		
		return encodeBase62(cipherToken);
	}
	
	/**
	 * Fully decrypt a List encrypted with encryptList
	 * 
	 * @param cipherText is text encrypted with encryptList
	 * @param key is the AES decryption key to use
	 * 
	 * @throws BadTokenException if 
	 */
	public static List<String> decryptList(String cipherText, byte[] key) throws GeneralSecurityException
	{
		String base64Token = CipherUtil.decodeBase62(cipherText);
		String plainToken = CipherUtil.decrypt(base64Token, key);
		
		StringTokenizer tok = new StringTokenizer(plainToken, ":");
		
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


	
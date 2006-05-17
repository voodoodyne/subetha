/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.admin.i;

import java.security.GeneralSecurityException;
import java.util.List;
import javax.ejb.Local;

/**
 * Encrypts and decrypts strings using an internal key.  The key
 * will be randomly generated but otherwise remains constant.
 * 
 * @author Jeff Schnitzer
 */
@Local
public interface Encryptor
{
	/** */
	public static final String JNDI_NAME = "subetha/Encryptor/local";

	/**
	 * Encrypts some bytes.  It will be AES encrypted to binary.
	 */
	public byte[] encrypt(byte[] plainText);
	
	/**
	 * Decrypts bytes encrypted with encrypt().
	 */
	public byte[] decrypt(byte[] cipherText) throws GeneralSecurityException;
	
	/**
	 * Encrypts a string.  It will be AES encrypted to binary.
	 */
	public byte[] encryptString(String plainText);
	
	/**
	 * Decrypts a bytes that were encrypted with encryptString().
	 */
	public String decryptString(byte[] cipherText) throws GeneralSecurityException;

	/**
	 * Decrypts a bytes that were encrypted with encryptString().
	 * 
	 * @throws ExpiredException if the token is older than maxAgeMillis
	 */
	public String decryptString(byte[] cipherText, long maxAgeMillis) throws GeneralSecurityException, ExpiredException;

	/**
	 * Encrypts a list of strings.
	 */
	public byte[] encryptList(List<String> parts);
	
	/**
	 * Decrypts bytes that were encrypted with encryptList().
	 */
	public List<String> decryptList(byte[] cipherText) throws GeneralSecurityException;
	
	/**
	 * Decrypts bytes that were encrypted with encryptList(), checking
	 * to make sure the encryption was recent.
	 * 
	 * @throws ExpiredException if the age of the token exceeds maxAgeMillis.
	 */
	public List<String> decryptList(byte[] cipherText, long maxAgeMillis) throws GeneralSecurityException, ExpiredException;
}

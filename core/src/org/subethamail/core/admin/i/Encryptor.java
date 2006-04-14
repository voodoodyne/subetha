/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
 */

package org.subethamail.core.admin.i;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.ejb.Local;

import org.subethamail.core.acct.i.BadTokenException;

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
	 * @throws BadTokenException if the age of the list exceeds maxAgeMillis.
	 */
	public List<String> decryptList(byte[] cipherText, long maxAgeMillis) throws GeneralSecurityException, ExpiredException;
}

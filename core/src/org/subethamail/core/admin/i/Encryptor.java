/*
 * $Id: Receptionist.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/recep/i/Receptionist.java $
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
	 * Encrypts a string.  It will be AES encrypted to binary.
	 */
	public byte[] encrypt(String plainText);
	
	/**
	 * Decrypts bytes encrypted with encrypt().
	 */
	public String decrypt(byte[] cipherText) throws GeneralSecurityException;
	
	/**
	 * Encrypts a string.  It will be AES encrypted and Base64 encoded.
	 */
	public String encryptString(String plainText);
	
	/**
	 * Decrypts a string that was encrypted with encryptString().
	 */
	public String decryptString(String cipherText) throws GeneralSecurityException;

	/**
	 * Encrypts a list of strings into a single Base64-encoded String.
	 */
	public String encryptList(List<String> parts);
	
	/**
	 * Decrypts a string that was encrypted with encryptList().
	 */
	public List<String> decryptList(String cipherText) throws GeneralSecurityException;
}

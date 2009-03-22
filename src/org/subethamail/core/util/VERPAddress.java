/*
 * $Id: VERPAddress.java 907 2007-02-07 09:06:00Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/branches/resin/core/src/org/subethamail/core/util/VERPAddress.java $
 */

package org.subethamail.core.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class which abstracts the handling of VERP bounces.  This can
 * separate the VERP token from the original address.  It can
 * also assemble an address from an email and a token.
 * 
 * A VERP'd address looks like:
 * 
 * announce-verp-THISISTHETOKEN-bounce@domain.com
 * 
 * The extracted email will be announce@domain.com.
 * 
 * Note that the token is a lowercase hex-encoded string.
 * This is because some evil MTAs (Postfix grrr) force
 * the mailbox part of an address to lowercase, screwing
 * up any attempt at base64 encoding.
 * 
 * @author Jeff Schnitzer
 */
public class VERPAddress
{
	/** */
	private static Log log = LogFactory.getLog(VERPAddress.class);
	
	/** This is the suffix of all VERP'd addresses, plus the '@' */
	public static final String SUFFIX = "-bounce@";
	
	/** This preceeds the token in the address */
	public static final String LEAD = "-verp-";
	
	/** */
	String email;
	String tokenHex;
	
	/** */
	VERPAddress(String email, String tokenHex)
	{
		this.email = email;
		this.tokenHex = tokenHex;
	}
	
	/**
	 * @return the "normal" email address, without the verp information.
	 *  This will be the mailing list address that the mail was from. 
	 */
	public String getEmail() { return this.email; }
	
	/**
	 * Lazily decodes the token.
	 * 
	 * @return the token that was extracted from the VERP address. 
	 */
	public byte[] getToken()
	{
		try
		{
			return (byte[])Hex.decodeHex(this.tokenHex.toCharArray());
		}
		catch (DecoderException ex) { throw new RuntimeException(ex); }
	}

	/**
	 * @return The Hex encoded string.
	 */
	public String getRawToken()
	{
		return this.tokenHex;
	}

	/**
	 * @return null if the address was not a VERP'ed address.  
	 */
	public static VERPAddress getVERPBounce(String addy)
	{
		// Note the suffix includes the @
		int suffixIndex = addy.indexOf(SUFFIX);
		if (suffixIndex < 0)
			return null;
		
		int leadIndex = addy.lastIndexOf(LEAD, suffixIndex);
		if (leadIndex < 0)
			return null;
		
		// Watch out for blah-verp-bounce@foo.bar
		if (leadIndex + LEAD.length() >= suffixIndex)
			return null;

		String email = addy.substring(0, leadIndex) + '@' + addy.substring(suffixIndex+SUFFIX.length());
		String tokenHex = addy.substring(leadIndex+LEAD.length(), suffixIndex);
		
		if (log.isDebugEnabled())
			log.debug(addy + " becomes " + email + "/" + tokenHex);
		
		return new VERPAddress(email, tokenHex);
	}
	
	/**
	 * @return an email address encoded with the binary token.
	 */
	public static String encodeVERP(String email, byte[] token)
	{
		int atIndex = email.indexOf('@');
		
		return email.substring(0, atIndex)
			+ LEAD + new String(Hex.encodeHex(token)) + SUFFIX 
			+ email.substring(atIndex+1);
	}
}


	
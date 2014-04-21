package org.subethamail.core.util;

import java.util.logging.Level;

import lombok.extern.java.Log;


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
@Log
public class VERPAddress
{
	/** This is the suffix of all VERP'd addresses, plus the '@' */
	public static final String SUFFIX = "-b@";
	
	/** This preceeds the token in the address */
	public static final String LEAD = "-verp-";
	
	/** */
	String email;
	String tokenEncoded;
	
	/** */
	VERPAddress(String email, String tokenBase62)
	{
		this.email = email;
		this.tokenEncoded = tokenBase62;
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
		return Base62.decode(this.tokenEncoded);
	}

	/**
	 * @return The Base62 encoded string.
	 */
	public String getRawToken()
	{
		return this.tokenEncoded;
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
		String tokenBase62 = addy.substring(leadIndex+LEAD.length(), suffixIndex);
		
		if (log.isLoggable(Level.FINE))
			log.log(Level.FINE,"{0} becomes {1}/{2}", new Object[]{addy,email,tokenBase62});
		
		return new VERPAddress(email, tokenBase62);
	}
	
	/**
	 * @return an email address encoded with the binary token.
	 */
	public static String encodeVERP(String email, byte[] token)
	{
		int atIndex = email.indexOf('@');
		
		return email.substring(0, atIndex)
			+ LEAD + new String(Base62.encode(token)) + SUFFIX 
			+ email.substring(atIndex+1);
	}
}


	
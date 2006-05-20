/*
 * $Id$
 * $URL$
 */

package org.subethamail.core.util;

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
 * The extracted email will be announce@domain.com
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
	byte[] token;
	
	/** */
	VERPAddress(String email, byte[] token)
	{
		this.email = email;
		this.token = token;
	}
	
	/**
	 * @return the "normal" email address, without the verp information.
	 *  This will be the mailing list address that the mail was from. 
	 */
	public String getEmail() { return this.email; }
	
	/**
	 * @return the token that was extracted from the VERP address. 
	 */
	public byte[] getToken() { return this.token; }
	
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
		String token62 = addy.substring(leadIndex+LEAD.length(), suffixIndex);
		
		if (log.isDebugEnabled())
			log.debug(addy + " becomes " + email + "/" + token62);
		
		return new VERPAddress(email, Base62.decode(token62));
	}
	
	/**
	 * @return an email address encoded with the token.
	 */
	public static String encodeVERP(String email, byte[] token)
	{
		int atIndex = email.indexOf('@');
		
		return email.substring(0, atIndex)
			+ LEAD + Base62.encode(token) + SUFFIX 
			+ email.substring(atIndex+1);
	}
}


	
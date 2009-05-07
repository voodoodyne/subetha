package org.subethamail.core.util;

import java.security.Principal;

import com.caucho.server.security.CachingPrincipal;

/**
 * Subetha Principal (based on CachingPrincipal)
 * 
 * @author Scott Hernandez
 *
 */
public class SubethaPrincipal extends CachingPrincipal {
	/**	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param id The {@link Principal} id
 	 * @param email The email address they logged in with.
	 */
	public SubethaPrincipal(String id, String email){
		super(id);
		this.email = email;
	}
	/** The principal's email	 */
	private String email;
	/**
	 * Does what is says.
	 * @return The Principal's email
	 */
	public String getEmail(){
		return this.email;
	}
}
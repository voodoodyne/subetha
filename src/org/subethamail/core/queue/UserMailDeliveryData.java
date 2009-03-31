package org.subethamail.core.queue;

import java.io.Serializable;

public class UserMailDeliveryData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Long userId, mailId;
	
	public UserMailDeliveryData(Long m, Long u){this.userId=u;this.mailId=m;}
	
	public Long getMailId(){return this.mailId;}
	public Long getUserId(){return this.userId;}
}

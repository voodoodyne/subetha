/*
 * $Id: Validator.java 105 2006-02-27 10:06:27Z jeff $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/util/Geometry.java,v $
 */

package org.subethamail.common.valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * For validating data at all tiers.  These constants also define
 * the length of columns in the database.
 *
 * @author Jeff Schnitzer
 */
public class Validator
{
	/** */
	private static Log log = LogFactory.getLog(Validator.class);

	// Employee
	public static final int MAX_EMPLOYEE_LAST_NAME = 255;
	public static final int MAX_EMPLOYEE_FIRST_NAME = 255;
	public static final int MAX_EMPLOYEE_DISPLAY_NAME = 50;
	public static final int MAX_EMPLOYEE_LOGIN = 100;
	public static final int MAX_EMPLOYEE_PASSWORD = 100;
	
	// PurchaseOrder
	public static final int MAX_PURCHASE_ORDER_DESCRIPTION = 4000;
	
	// LineItem
	public static final int MAX_LINE_ITEM_NAME = 255;
	
	// Account
	public static final int MAX_ACCOUNT_NAME = 255;
	public static final int MAX_ACCOUNT_DEPARTMENT = 255;
	
	// WorkOrder
	public static final int MAX_WORK_ORDER_NAME = 255;
	
	// PaymentMethod
	public static final int MAX_PAYMENT_METHOD_NAME = 255;
	
	// Department
	public static final int MAX_DEPARTMENT_NAME = 255;
	
	// Vendor
	public static final int MAX_VENDOR_NAME = 255;
	public static final int MAX_VENDOR_PHONE = 20;
	public static final int MAX_VENDOR_FAX = 30;
	public static final int MAX_VENDOR_CONTACT = 30;
	
	// Address
	public static final int MAX_ADDRESS_LINE = 255;
	public static final int MAX_ADDRESS_CITY = 255;
	public static final int MAX_ADDRESS_STATE = 155;
	public static final int MAX_ADDRESS_ZIP = 100;
	public static final int MAX_ADDRESS_COUNTRY = 200;
	
	// VendorAddress
	public static final int MAX_VENDOR_ADDRESS_LINE = 255;
	public static final int MAX_VENDOR_ADDRESS_CITY = 30;
	public static final int MAX_VENDOR_ADDRESS_STATE = 30;
}



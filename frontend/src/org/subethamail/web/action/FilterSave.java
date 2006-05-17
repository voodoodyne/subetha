/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.Converter;
import org.subethamail.core.lists.i.EnabledFilterData;
import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.tagonist.propertize.Property;

/**
 * Saves a new or existing filter.  This doesn't work like normal
 * form processing because the model is so variable; the set of filter
 * parameters is dynamically defined.
 * 
 * The UI elements we work with:
 * 
 * If java.lang.Boolean, use checkbox
 * If subclass of java.lang.Enum, use combobox
 * Otherwise use a text box.  An exception will possibly occur on valueOf() when saving.
 * 
 * Note that String and Character must be special cased.
 * 
 * @author Jeff Schnitzer
 */
public class FilterSave extends AuthAction 
{
	/** */
	private static Log log = LogFactory.getLog(FilterSave.class);
	
	/** All the dynamic form data field names start with this */
	public static final String FORM_FIELD_NAME_PREFIX = "form:";
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** In */
		@Property Long listId;
		@Property String className;
		
		/** Out - the filter data raw from the backend */
		@Property EnabledFilterData filter;
		
		/**
		 * Out - any enums will have an entry here, allowing the UI to both
		 * realize that the param is an enum and to iterate through the values. 
		 */
		@Property Map<String, Enum[]> enumValues;
		
		/**
		 * In/Out - the form fields.  Key is param name, value is string version of value.
		 * This is a lot easier to use from JSTL than the EnabledFilterData, and allows
		 * bean population as well.
		 */
		@Property Map<String, String> form = new HashMap<String, String>();
		
		/**
		 * Initializes the enumValues from the filter.  Do this after you
		 * have initialized filter and before you are about to display the
		 * edit page (ie when starting edit or forwarding on error). 
		 */
		void initEnumValues()
		{
			this.enumValues = new HashMap<String, Enum[]>();
			
			for (FilterParameter filtParam: this.filter.getParameters())
			{
				Enum[] values = (Enum[])filtParam.getType().getEnumConstants();
				
				if (values != null)
					this.enumValues.put(filtParam.getName(), values);
			}
		}
	}
	
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		// This is specially required because BeanUtils doesn't have an escaping mechanism
		this.populateFormMap(model.form);
		
		// We need to convert each of the form data fields to the natural type.
		// To do that, we need to know the expected types.  Also, if there is an
		// error, we'll need the model set up for the forward back to the edit page.
		model.filter = Backend.instance().getListMgr().getFilter(model.listId, model.className);
		
		// We'll build the actual values into this collection
		Map<String, Object> args = new HashMap<String, Object>();
		
		for (FilterParameter param: model.filter.getParameters())
		{
			String stringValue = model.form.get(param.getName());
			if (stringValue != null)
			{
				try
				{
					args.put(param.getName(), Converter.valueOf(stringValue, param.getType()));
				}
				catch (NumberFormatException ex)
				{
					model.setError(param.getName(), "Must be a number");
				}
				catch (Exception ex)
				{
					// TODO:  make these error messages a little prettier.
					model.setError(param.getName(), ex.toString());
				}
			}
			else
			{
				// Special case; unchecked checkboxes don't come back with the form
				// data.  So assume any boolean values are false.
				if (param.getType().equals(Boolean.class) || param.getType().equals(Boolean.TYPE))
					args.put(param.getName(), Boolean.FALSE);
			}
		}
		
		if (model.getErrors().isEmpty())
			Backend.instance().getListMgr().setFilter(model.listId, model.className, args);
		else
			model.initEnumValues();
	}

	/**
	 * I had hoped to use the BeanUtils mapped properties to allow magical
	 * population of Model.form.  However, there is no easy way to escape
	 * names of filter param keys, so any that contain () or . will screw
	 * up the beanutils processor.  Instead we simply prefix all form names
	 * with "form:" and treat everything following as a key name.
	 */
	private void populateFormMap(Map<String, String> formMap)
	{
		Enumeration enu = this.getCtx().getRequest().getParameterNames();
		while (enu.hasMoreElements())
		{
			String key = (String)enu.nextElement();
			
			if (key.startsWith(FORM_FIELD_NAME_PREFIX))
			{
				String value = this.getCtx().getRequest().getParameter(key);
				formMap.put(key.substring(FORM_FIELD_NAME_PREFIX.length()), value);
			}
		}
		
	}
}

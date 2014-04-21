/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.action;

import org.subethamail.core.plugin.i.FilterParameter;
import org.subethamail.web.Backend;
import org.subethamail.web.action.FilterSave.Model;
import org.subethamail.web.action.auth.AuthAction;

/**
 * Pre-populates a model for FilterSave.  This is a very peculiar
 * action, @see GetFilterForEdit for more documentation.
 * 
 * @author Jeff Schnitzer
 */
public class GetFilterForEdit extends AuthAction 
{
	/** */
	public void initialize()
	{
		this.getCtx().setModel(new Model());
	}
	
	/** */
	public void execute() throws Exception
	{
		Model model = (Model)this.getCtx().getModel();
		
		model.filter = Backend.instance().getListMgr().getFilter(model.listId, model.className);
		
		// Set up the enum values so comboboxes work
		model.initEnumValues();
		
		// Set up the form values 
		for (FilterParameter filtParam: model.filter.getParameters())
		{
			// The form data
			Object value = model.filter.getArguments().get(filtParam.getName());
			if (value != null)
				model.form.put(filtParam.getName(), value.toString());
		}
	}
	
}

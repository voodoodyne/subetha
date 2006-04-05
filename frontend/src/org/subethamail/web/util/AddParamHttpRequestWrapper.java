package org.subethamail.web.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps the request and provides one additional http request paramter.
 * Not thread safe.
 */
@SuppressWarnings("deprecation")
public class AddParamHttpRequestWrapper extends HttpServletRequestWrapper
{
	/** */
	String paramName;
	String[] paramValue;
	
	/** Lazy initialized */
	Map<String, String[]> parameterMap;
	
	/** */
	public AddParamHttpRequestWrapper(HttpServletRequest request, String paramName, String paramValue)
	{
		super(request);
		
		this.paramName = paramName;
		this.paramValue = new String[] { paramValue };
	}
	
	/** */
	@Override
	public String getParameter(String name)
	{
		if (paramName.equals(name))
			return paramValue[0];
		else
			return super.getParameter(name);
	}
	
	/** */
	@Override
	@SuppressWarnings("unchecked")
	public Map getParameterMap()
	{
		if (this.parameterMap == null)
		{
			this.parameterMap = new HashMap<String, String[]>(super.getParameterMap());
			this.parameterMap.put(this.paramName, this.paramValue);
		}
		
		return this.parameterMap;
	}
	
	/** */
	@Override
	public Enumeration getParameterNames()
	{
		throw new UnsupportedOperationException();
	}
	
	/** */
	@Override
	public String[] getParameterValues(String name)
	{
		if (this.paramName.equals(name))
			return this.paramValue;
		else
			return super.getParameterValues(name);
	}
}
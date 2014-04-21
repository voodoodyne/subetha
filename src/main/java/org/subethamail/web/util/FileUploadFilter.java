/*
 * $Id: FileUploadFilter.java 110 2006-02-28 06:59:40Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/frontend/src/com/blorn/web/util/FileUploadFilter.java $
 */

package org.subethamail.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.subethamail.common.EnumerationAdapter;

/**
 * Uses the jakarta commons-fileupload code as a filter, presenting
 * what looks like a more-or-less normal http request.  Theoretically
 * commons-fileupload 1.1 will have a filter class builtin which can
 * replace this, but that code is not available yet.
 */
public class FileUploadFilter extends AbstractFilter
{
	/**
	 * Maximum size of an upload, currently 512MB.
	 */
	public static final int MAX_UPLOAD_BYTES = 1024 * 1024 * 512;

	/**
	 * Attribute in the request where the files are stored.  Clients to
	 * this filter should use getFiles()
	 */
	protected static final String ATTR_FILES = FileUploadFilter.class.getName() + ".files";

	/**
	 * Attribute in the request where an exception will be stored.  This
	 * will only be the case if an exception was thrown during processing.
	 */
	protected static final String ATTR_EXCEPTION = FileUploadFilter.class.getName() + ".exception";

	/**
	 * Use this method to obtain the files extracted from the multipart request.
	 * Form fields will not show up in this list.
	 *
	 * @throws any exception that might have been thrown during the processing of the
	 *  upload.  The stack trace will seem a little odd.
	 */
	@SuppressWarnings("unchecked")
	public static List<FileItem> getFiles(HttpServletRequest request) throws FileUploadException
	{
		FileUploadException ex = (FileUploadException)request.getAttribute(ATTR_EXCEPTION);
		if (ex != null)
			throw ex;

		List<FileItem> files = (List<FileItem>)request.getAttribute(ATTR_FILES);
		if (files == null)
			throw new IllegalStateException("Missing files, perhaps filter not configured or form enctype was wrong");

		return files;
	}

	/**
	 * Filter the current request. If it is a multipart request, parse it and
	 * wrap it before chaining to the next filter or servlet. Otherwise, pass
	 * it on untouched.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		if (!FileUploadBase.isMultipartContent(request))
		{
			chain.doFilter(request, response);
			return;
		}

		try
		{
			//FileItemFactory fact = new DiskFileItemFactory();
			//FileUpload upload = new ServletFileUpload(fact);
			DiskFileUpload upload = new DiskFileUpload();
			upload.setSizeMax(MAX_UPLOAD_BYTES);

			List<FileItem> items = upload.parseRequest(request);

			Map<String, String[]> params = new HashMap<String, String[]>();
			List<FileItem> files = new ArrayList<FileItem>(items.size());

			for (FileItem item: items)
			{
				if (item.isFormField())
				{
					// Add it to the array in the params, creating the array if necessary
					String[] array = params.get(item.getFieldName());
					if (array == null)
					{
						array = new String[] { item.getString() };
					}
					else
					{
						String[] newArray = new String[array.length + 1];
						System.arraycopy(array, 0, newArray, 0, array.length);
						newArray[newArray.length - 1] = item.getString();

						array = newArray;
					}

					params.put(item.getFieldName(), array);
				}
				else
					files.add(item);
			}

			request.setAttribute(ATTR_FILES, files);
			HttpServletRequest wrapped = new RequestWrapper(request, params);

			chain.doFilter(wrapped, response);
		}
		catch (FileUploadException ex)
		{
			// Just save the exception for later.
			request.setAttribute(ATTR_EXCEPTION, ex);

			chain.doFilter(request, response);
		}
	}

	/**
	 * Wraps the request providing a set of params as if they were the
	 * normal servlet params.
	 */
	class RequestWrapper extends HttpServletRequestWrapper
	{
		/** */
		protected Map<String, String[]> params;

		/** */
		public RequestWrapper(HttpServletRequest orig, Map<String, String[]> params)
		{
			super(orig);

			this.params = params;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter(String name)
		{
			String[] val = this.params.get(name);
			if (val == null)
				return null;
			else
				return val[0];
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterMap()
		 */
		@Override
		public Map<String, String[]> getParameterMap()
		{
			return this.params;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterNames()
		 */
		@Override
		public Enumeration<String> getParameterNames()
		{
			return new EnumerationAdapter<String>(this.params.keySet().iterator());
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
		 */
		@Override
		public String[] getParameterValues(String name)
		{
			return this.params.get(name);
		}
	}
}

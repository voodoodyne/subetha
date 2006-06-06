/*
 * $Id: GetThreads.java 551 2006-05-25 10:36:56Z jon $
 * $URL: http://subetha.tigris.org/svn/subetha/trunk/frontend/src/org/subethamail/web/action/GetThreads.java $
 */

package org.subethamail.web.action;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.web.Backend;
import org.subethamail.web.action.auth.AuthAction;
import org.subethamail.web.model.ErrorMapModel;
import org.subethamail.web.util.FileUploadFilter;
import org.tagonist.propertize.Property;

/**
 * Processes an mbox of messages and inserts them into the list.
 * 
 * @author Scott Hernandez
 */
public class UploadMBOX extends AuthAction
{
	/** */
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(UploadMBOX.class);
	
	/** */
	public static class Model extends ErrorMapModel
	{
		/** If this is not null, create a new role */
		@Property Long listId;

		@Property List<String> messageSubjects;
		
		@Property int countImported;
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

		List<FileItem> files;
		try
		{
			files = FileUploadFilter.getFiles(this.getCtx().getRequest());
		}
		catch (FileUpload.SizeLimitExceededException ex)
		{
			int maxK = FileUploadFilter.MAX_UPLOAD_BYTES / (1024 * 1024);
			model.setError("files", "The file was too big.  Maximum of " + maxK + "MB.");
			return;
		}
		catch (FileUploadException ex)
		{
			model.setError("files", "There was an error uploading the file:  " + ex.getMessage());
			return;
		}

		if (files.isEmpty())
		{
			log.error("Upload succeeded but no files found.  Weird.");
			model.setError("files", "Upload failed");
			return;
		}

		FileItem file = files.get(0);

		model.countImported = Backend.instance().getArchiver().importMessages(model.listId, file.getInputStream());
	}	
}
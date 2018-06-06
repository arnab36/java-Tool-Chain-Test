package com.ibm.contracts.advisor.handler;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class ApacheFileUpload {
	
	public static File apacheUpload(HttpServletRequest request, HttpServletResponse response) throws Exception	{	
		// Create a factory for disk-based file items
	
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//factory.setFileCleaningTracker(fileCleaningTracker);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		List <FileItem> items = upload.parseRequest(request);
		Iterator <FileItem>iter = items.iterator();

		String type = "";
		String fileName = "";
		File file = null;

		while (iter.hasNext()){
			FileItem item = (FileItem) iter.next();
	
			if (item.isFormField() && item.getFieldName().equalsIgnoreCase("type")) {
				type =item.getString();
			} 
	
			if(!item.isFormField() && item.getFieldName().equalsIgnoreCase("file")) {
				//removes path information. 
				fileName = FilenameUtils.getName(item.getName());
				file = new File(fileName);
				item.write(file);
			}
		}
		return file;
	}
	
	
}

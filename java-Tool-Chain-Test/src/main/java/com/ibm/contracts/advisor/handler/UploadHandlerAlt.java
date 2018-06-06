package com.ibm.contracts.advisor.handler;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadHandlerAlt {

	
public static File uploadAlt(HttpServletRequest request, HttpServletResponse response, PrintWriter out){
		
		File file = null;
		String fileName ="";
		String filePath = "";
		
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			List<FileItem> fields = upload.parseRequest(request);
			//out.println("Number of fields: " + fields.size() + "<br/><br/>");
			Iterator<FileItem> it = fields.iterator();
			if (!it.hasNext()) {
				out.println("No fields found");
				return null;
			}
			//out.println("<table border=\"1\">");
			while (it.hasNext()) {
				//out.println("<tr>");
				FileItem fileItem = it.next();
				boolean isFormField = fileItem.isFormField();
				if (isFormField) {
					out.println("<td>regular form field</td><td>FIELD NAME: " + fileItem.getFieldName() + 
							"<br/>STRING: " + fileItem.getString()
							);
					out.println("</td>");
				} else {
					
					fileName = fileItem.getName();
					// Write the file
		            if( fileName.lastIndexOf("\\") >= 0 ){
		               file = new File( filePath + 
		               fileName.substring( fileName.lastIndexOf("\\"))) ;
		            }else{
		               file = new File( filePath + 
		               fileName.substring(fileName.lastIndexOf("\\")+1)) ;
		            }
		            try {
						fileItem.write( file ) ;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            out.println("Uploaded Filename: " + fileName + "<br>");
					
			}
			out.println("</table>");
		}} catch (FileUploadException e) {
			e.printStackTrace();
		}
		return file;
		
	}
	
}

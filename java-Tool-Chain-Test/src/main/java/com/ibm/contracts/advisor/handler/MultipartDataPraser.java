package com.ibm.contracts.advisor.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.ibm.contracts.advisor.vo.UploadVO;

public class MultipartDataPraser {

	
	public static UploadVO processMultipartFormData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		UploadVO newObj = new UploadVO();
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// Set overall request size constraint
	//	upload.setSizeMax(yourMaxRequestSize);
		
		List  items = upload.parseRequest(request);
		
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = (FileItem) iter.next();

		    if (item.isFormField()) {
		      String name = item.getFieldName();//text1
		      String value = item.getString();
		      System.out.println("name :: "+ name);
		      System.out.println("value :: "+ value);
		      
		      if(name.equalsIgnoreCase("contractTypeDocConvert")) {
		    	  newObj.setContractType(value);
		      }
		      
		      if(name.equalsIgnoreCase("input_file_name_doc")) {
		    	 newObj.setFileName(value);
		      }
		      
		      if(name.equalsIgnoreCase("user_id")) {
			      newObj.setUserId(value);
			  }
		      
		    } else {		    	
		    	File uploadedFile = streamToFile(item);
		    	newObj.setInputFile(uploadedFile); 
		    }
		}
		
		return newObj;
	}
	
	
	/*
	 *  Converting item to File pointer via input stream write
	 */
	public static File streamToFile(FileItem item) throws Exception{
		
		String fileName = item.getName();
		 if (fileName != null) {
		     fileName = FilenameUtils.getName(fileName);
		 }
		 
		File file = new File(fileName);
		InputStream is = item.getInputStream();        
        OutputStream os = new FileOutputStream(file);
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        //read from is to buffer
        while((bytesRead = is.read(buffer)) !=-1){
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        //flush OutputStream to write any buffered data to file
        os.flush();
        os.close(); 
        return file;
	}
	
}



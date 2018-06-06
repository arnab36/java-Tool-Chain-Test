package com.ibm.contracts.advisor.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.util.SOP;
import org.apache.commons.lang.StringUtils;

public class UploadHandler {
	/*
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file;
	String fileName ="";
	*/
	
	
	 public static File upload(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException{
	        // gets absolute path of the web application
	    	File newFile = null;
	    	String fileName = null;
	        String appPath = request.getServletContext().getRealPath("/");
	        String savePath = ((appPath != null) ? appPath : "")  + File.separator + Constants.SAVE_DIR;
	        // creates the save directory if it does not exists
	        File fileSaveDir = new File(savePath);
	        if (!fileSaveDir.exists()) {
	            fileSaveDir.mkdir();
	        }
	         
	        for (Part part : request.getParts()) {
	        	System.out.println("part :: "+part);
	            String strFileName = extractFileName(part); 
	           // String strFileName = fileName; 
	           // String strFileName = "temp.pdf";
	            // refines the fileName in case it is an absolute path
	            if(strFileName != null && StringUtils.isNotEmpty(strFileName))
	            {
	                File f = new File(strFileName);
	                fileName = f.getName();
	                fileName =  fileName.replaceAll("[^\\x00-\\x7F]", ""); 
	                fileName = fileName.replaceAll("[']", ""); 
	                HttpSession session = request.getSession(true);
	                 String userId = (String)session.getAttribute("userId");
	              //  System.out.println("User Id :: "+ userId.substring(0, userId.indexOf('@')));
	                SOP.printSOPSmall("User Id :: "+ userId.substring(0, userId.indexOf('@')));
	                	                
	            	newFile = new File(savePath + File.separator + userId.substring(0, userId.indexOf('@')) + "_" + System.currentTimeMillis() + "_" + fileName);
	            	newFile.createNewFile();
	    			try {
	    				InputStream inputStream = part.getInputStream();
	    				int read = 0;
	    				byte[] bytes = new byte[1024];
	    	            OutputStream outputStream = new FileOutputStream(newFile);
	    	            while ((read = inputStream.read(bytes)) != -1) {
	    					outputStream.write(bytes, 0, read);
	    				}
	    				outputStream.close();
	    			} catch (java.io.FileNotFoundException e) {
	    			} 
	    			catch (java.io.IOException ex) {
	    			}
	               
	            }	
	        }
	        return newFile;
		}
	 
	 
	 
	 public static File uploadWithExtraParameter(String fileName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        // gets absolute path of the web application
	    	File newFile = null;
	    	//String fileName = null;
	        String appPath = request.getServletContext().getRealPath("/");
	        String savePath = ((appPath != null) ? appPath : "")  + File.separator + Constants.SAVE_DIR;
	        // creates the save directory if it does not exists
	        File fileSaveDir = new File(savePath);
	        if (!fileSaveDir.exists()) {
	            fileSaveDir.mkdir();
	        }
	     
	        for (Part part : request.getParts()) {
	        	System.out.println("part size :: "+part.getSize());
	        	System.out.println("part Stream:: "+part.getInputStream().read());
	           // String strFileName = extractFileName(part); 
	            String strFileName = fileName; 
	           // String strFileName = "temp.pdf";
	            // refines the fileName in case it is an absolute path
	            if(strFileName != null && StringUtils.isNotEmpty(strFileName))
	            {
	                File f = new File(strFileName);
	                fileName = f.getName();
	                fileName =  fileName.replaceAll("[^\\x00-\\x7F]", ""); 
	                fileName = fileName.replaceAll("[']", ""); 
	                HttpSession session = request.getSession(true);
	              //  String strUserName = (String)session.getAttribute("userId");
	              //  String userId = (String)request.getParameter("userId");
	                String userId = (String)session.getAttribute("userId");
	              //  System.out.println("User Id :: "+ userId.substring(0, userId.indexOf('@')));
	                SOP.printSOPSmall("User Id :: "+ userId.substring(0, userId.indexOf('@')));
	                	                
	            	newFile = new File(savePath + File.separator + userId.substring(0, userId.indexOf('@')) + "_" + System.currentTimeMillis() + "_" + fileName);
	            	newFile.createNewFile();
	    			try {
	    				InputStream inputStream = part.getInputStream();
	    				int read = 0;
	    				byte[] bytes = new byte[1024];
	    	            OutputStream outputStream = new FileOutputStream(newFile);
	    	            while ((read = inputStream.read(bytes)) != -1) {
	    					outputStream.write(bytes, 0, read);
	    				}
	    				outputStream.close();
	    			} catch (java.io.FileNotFoundException e) {
	    			} 
	    			catch (java.io.IOException ex) {
	    			}
	               
	            }	
	        }
	        return newFile;
		}
		
	    /**
	     * Extracts file name from HTTP header content-disposition
	     */
	  private static String extractFileName(Part part) {
		  System.out.println("part11 :: "+part); 
		 // String contentDisp = part.getHeader("CONTENT_DISPOSITION");
	        String contentDisp = part.getHeader("content-disposition");
	        String[] items = contentDisp.split(";");
	        for (String s : items) {
	            if (s.trim().startsWith("filename")) {
	                return s.substring(s.indexOf("=") + 2, s.length()-1);
	            }
	        }
	        return "";
	    }
	  
	  
	 
	  
	  
	    
}

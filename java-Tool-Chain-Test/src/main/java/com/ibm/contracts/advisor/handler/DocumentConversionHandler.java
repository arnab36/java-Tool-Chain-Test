package com.ibm.contracts.advisor.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonParser;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import com.ibm.watson.developer_cloud.document_conversion.v1.util.ConversionUtils;
import com.ibm.watson.developer_cloud.http.ServiceCall;

public class DocumentConversionHandler implements Constants{
	
	public static ConvertReturn convertFile(File targetFile){
		
		double kilobytes = (targetFile.length() / 1024);
		//System.out.println("file size in kb "+kilobytes);
		ConvertReturn c=new ConvertReturn(null,null,null);
		
		if(kilobytes<5.0){
			c.setError("file size is too small");
			c.setLog("not able to process file");
			return c;
		}
			
		String dateStr = "2017-04-17";
		SOP.printSOPSmall("inside conversion");
		DocumentConversion documentConversion = null;
		
		if(!CISCOVCAP){
			if(VCAPDOCUMENTCONVERSION){
				//VcapSetupParser.getDocumentConversionCredentials(VcapSetupParser.VCAPUtils());
				documentConversion = new DocumentConversion(dateStr, VcapSetupParser.VCAPDOCCONVERSION_userID, VcapSetupParser.VCAPDOCCONVERSION_password);
			}else {
				documentConversion = new DocumentConversion(dateStr, DOC_CONV_1, DOC_CONV_2);
			}			
		}else{
			documentConversion = new DocumentConversion(dateStr, CISCO_DOC_CONV_1, CISCO_DOC_CONV_2);
		}
		
	//	System.out.println("object is :"+documentConversion);
		String filestr = targetFile.getPath();
		
		String mediatype = getMediaType(filestr);
		//System.out.println(filestr+"---------"+mediatype);
		Answers answers = null;
		//TODO CUSTOM CONFIGURATION TO BE ADDED.
		try {			
			JsonParser parser = new JsonParser();
			ServiceCall calltwo =  documentConversion.convertDocumentToAnswer(targetFile, mediatype);		
			if(calltwo != null) {
				answers = (Answers)calltwo.execute();
			}
			if(answers==null)
			{
				c.setError("Document conversion server is not reachable");
				c.setLog("error in document conversion");
				return c;
			}
			
		targetFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			c.setError(e.toString());
			c.setLog("error in document conversion");
			return c;
		}
		c.setAnswer(answers);
		c.setLog("document conversion successful");
		return c;
		
		
	}
	
	// text/html, text/xhtml+xml, application/pdf, application/msword, and application/vnd.openxmlformats-officedocument.wordprocessingml.document
		private static String getMediaType(String filestr){
			//Return different mime types based on supported files (extensions)
			String ext = FilenameUtils.getExtension(filestr);
			if("html".equals(ext)){
				return "text/html";
			} else if("doc".equals(ext)){
				return "application/msword";
			} else if("docx".equals(ext)) {	
				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			} else if("pdf".equals(ext)){
				return "application/pdf";
			}else{
				return null;
			}
			
		}

	
	

}



package com.ibm.contracts.advisor.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.dao.implementation.DocumentsDAO;
import com.ibm.contracts.advisor.dao.implementation.StrategyDAO;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.handler.DocumentConversionHandler;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.handler.UploadHandler;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.util.ClassHierarchyConvert;
import com.ibm.contracts.advisor.util.DocumentSplitSentence;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.SessionData;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.JsonAndHTML;
import com.ibm.contracts.advisor.vo.StrategyObject;


@WebServlet(urlPatterns = { "/UploadMappingConfigServlet" }, initParams = {
		@WebInitParam(name = "saveDir", value = "uploadFiles"),
		@WebInitParam(name = "allowedTypes", value = "pdf,doc,docx") }

)



@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50 // 50MB
)


/**
 *   The following servlet with its post method will take a excel file as input and then will convert it 
 *   to a json of class Hierarchy and upload it to the object storage.
 *    
 * */

public class UploadMappingConfigServlet extends HttpServlet implements Constants {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("No do get method");
	}

	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		StringBuffer errorMsg = new StringBuffer();
		StringBuffer logMsg = new StringBuffer();
		boolean isSuccess = false;
		
		ServletContext sc = this.getServletContext();
		SessionData sd = new SessionData();

		HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		
		System.out.println("============= Inside UploadTemplateServlet ==================== ");
		
		String templateType2 = request.getParameter("templateType2");
		
		// MP or MS
		String contractType = request.getParameter("contractType2");
		
		// Static, Dynamic or General
		String scoreType = request.getParameter("scoreType2");
		
		
		String fileName = request.getParameter("config_file_name");		
		
		// FG or Non FG
		String documentType = request.getParameter("documentType2");	
		
		System.out.println("contractType:" + contractType);		
		System.out.println("templateType1:" + templateType2);
		System.out.println("scoreType::" + scoreType);
		System.out.println("fileName::" + fileName);
		
		// Not required
		String templateVersion = request.getParameter("templateVersion2");
		// Not required
		System.out.println("templateVersion::" + templateVersion);
		
		String outputFileName="";
		String changedContracyType="";
		String changedDocumentType = "";
		String changedScoreType="";
		
		if(contractType.equalsIgnoreCase("Managed Projects")) {
			changedContracyType = "MP";
		}else {
			changedContracyType = "MS";
		}
		System.out.println("changedContracyType :: "+ changedContracyType);
		
		if(documentType.equalsIgnoreCase("FG")) {
			changedDocumentType = "FG_";
		}else {
			changedDocumentType = "";
		}
		System.out.println("changedDocumentType :: "+ changedDocumentType);
		
		if(scoreType.equalsIgnoreCase("Static")) {
			changedScoreType = "STATIC";
		}else if(scoreType.equalsIgnoreCase("Dynamic")){
			changedScoreType = "DYNAMIC";
		}else {
			changedScoreType="GEN";
		}		
		
		System.out.println("changedScoreType :: "+ changedScoreType);
		
		
			
		// The name by whcih it will be saved in object storage
		outputFileName = "classHierarchyCISCO_"+changedDocumentType+changedScoreType+"_"+changedContracyType+".json";
		System.out.println("outputFileName :: "+outputFileName);
		
		File file = null;		
		try {
			file = UploadHandler.upload(request, response);
		} catch (Exception e1) {
			errorMsg.append("Error in file upload\n" + e1.toString());
			e1.printStackTrace();
		}
		
		if (file != null) { 
			if(contractType.equals("Dynamic")){
				try {
					ClassHierarchyConvert.createSingleLevelClassHierarchy(
							file.toString(), errorMsg, outputFileName);		
										
				}catch(Exception e){
					System.out.println("File to JSON convert problem.");
					errorMsg.append(e.toString());
				}
			}else {
				try {
					ClassHierarchyConvert.createMultilevelClasshierarchy(
							file.toString(), errorMsg, outputFileName);		
				}catch(Exception e){
					System.out.println("File to JSON convert problem.");
					errorMsg.append(e.toString());
				}
				
			}			
			
		}else {
			errorMsg.append("Error in file upload.");
		}
		
		try {
			SOP.printSOPSmall("Now responding ...... :: Successful");
			JsonRespond.uploadServletResopnse(logMsg.toString(),
					errorMsg.toString(), response,
					fileName, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}










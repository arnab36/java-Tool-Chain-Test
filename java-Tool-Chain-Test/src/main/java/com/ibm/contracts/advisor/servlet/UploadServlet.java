package com.ibm.contracts.advisor.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.FieldGlass.STATIC.FieldGlassStatic;
import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.dao.implementation.DocumentsDAO;
import com.ibm.contracts.advisor.dao.implementation.StrategyDAO;
import com.ibm.contracts.advisor.driver.PrepareGeneralJSON;
import com.ibm.contracts.advisor.handler.ApacheTikaHandler;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.handler.MultipartDataPraser;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.threadrunner.UploadServletFirstRunner;
import com.ibm.contracts.advisor.threadrunner.UploadServletSecondRunner;
import com.ibm.contracts.advisor.util.DocumentSplitSentence;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.SessionData;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.DocumentConversionObject;
import com.ibm.contracts.advisor.vo.JsonAndHTML;
import com.ibm.contracts.advisor.vo.StrategyObject;
import com.ibm.contracts.advisor.vo.UploadServletFirstOPVO;
import com.ibm.contracts.advisor.vo.UploadServletSecondOPVO;
import com.ibm.contracts.advisor.vo.UploadVO;


/*
 @WebServlet(urlPatterns = { "/UploadServlet" }, initParams = {
		@WebInitParam(name = "saveDir", value = "uploadFiles"),
		@WebInitParam(name = "allowedTypes", value = "pdf,doc,docx") }

)*/



@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50 // 50MB
)


public class UploadServlet extends HttpServlet implements Constants {

	private static final long serialVersionUID = 1L;
	PrintWriter out;
	private String templateFileName = null, staticTemplateName = null;
	StringBuffer errorMsg = new StringBuffer();

	/**
	 * 
	 * The following DoGet method will take the strategy info of a particular
	 * admin from Object storage and send them in back end for display in
	 * dropdown.
	 * 
	 * */
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userIdStrategy");
		String role = request.getParameter("userRole");

		JSONObject strategyInfoHierarchy = new JSONObject();
		JSONObject strategyInfo = null;
		if (role.equalsIgnoreCase("A")) {
			try {
				strategyInfo = Util.getJSONObject(ObjectStoreHandler
						.getFileStr(userId + "_strategy.json",
								STRATEGY_CONTAINER));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Getting the strategy Hierarchy
		try {
			 strategyInfoHierarchy = getStrategyInformationFromDasdDB(
						userId, role);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			if (role.equalsIgnoreCase("A")) {
				
				if(strategyInfoHierarchy == null){
					strategyInfoHierarchy = new JSONObject(); 
				}
				
				JsonRespond.createStrategyHierarchyResponseJSONAdmin(
						strategyInfoHierarchy.toJSONString(),
						strategyInfo.toJSONString(), response, 200);
			} else {
				
				if(strategyInfoHierarchy == null){
					strategyInfoHierarchy = new JSONObject(); 
				}
				
				JsonRespond.createLoginResponseJSON(
						strategyInfoHierarchy.toJSONString(), response, 200);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	// Do post method of Upload Servlet
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// out = response.getWriter();
		boolean success = false;
		StringBuffer logMsg = new StringBuffer();

		String docHTML = null;
		Cookie fiName = null;
		SOP.printSOPSmall("===============coming inside upload================");
		File newfile = null;
		ServletContext sc = this.getServletContext();
		SessionData sd = new SessionData();
		
		//HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		HttpSession session = request.getSession(true);
		String templateJson = null;
		String staticTemplateJson = null;	
		Document doc = null;
		
		/*    New File upload code ...        */
		UploadVO voObject = new UploadVO();
		if(newfile == null) {
			try {
				voObject = MultipartDataPraser.processMultipartFormData(request, response);				
				newfile = voObject.getInputFile();
				System.out.println("Size is 4th :: "+newfile.length());
				InputStream targetStream = new FileInputStream(newfile);
				ObjectStoreHandler.objectStore(targetStream, newfile.getName(), UPLOADCONFIGURATION_CONTAINER);
			}catch(Exception e) {
				e.printStackTrace();
			}	
		}
		
		// Getting other form data information 
		String contractType = 	voObject.getContractType();	
		String fileName = voObject.getFileName();
		String userIdfound = voObject.getUserId();
		/*          =====================================       */	
		System.out.println("Contract Type Found :: "+ contractType);
		System.out.println("input_file_name  Found :: "+ fileName);
		System.out.println("userIdfound  Found :: "+ userIdfound);
		
		/*       Renaming file  to an unique name      */		
		File file =new File(userIdfound.substring(0, userIdfound.indexOf('@')) + "_" + System.currentTimeMillis() + "_" + fileName);
		newfile.renameTo(file);
		
		// This will set up the workspace ID
		setUpWorkspaceID(contractType);
		System.out.println("templateFileName :: " + templateFileName);
		
		// putting file name in session
		if (file != null) {
			session.setAttribute("fileName", file.getName());
			request.setAttribute("fileName", file.getName());
			sd.setCookie("FILE_NAME", fiName, file.getName(), response);
		} else {
			errorMsg.append("Error in file Upload.");
		}

		// Checking user id in session
		String userid = null;
		try {
			userid = (String) session.getAttribute("userId");
			SOP.printSOPSmall("User Id :: " + userid);
		} catch (Exception e1) {
			errorMsg.append("Error in getting user id\n" + e1.toString());
			e1.printStackTrace();
		}

		/*    Processing file for document conversion   */
		if (file != null) {			
			ConvertReturn answers = null;
			ConvertReturn convertRes = null;
			System.out.println("Size is 5th :: "+file.length());

			
			// Branching for Field Glass
			if(contractType.equalsIgnoreCase(MPFG) || contractType.equalsIgnoreCase(MSFG)) {	
				templateJson = SetTemplates(templateFileName);
				System.out.println("templateJson is :: "+ templateJson);
				// Document conversion for Field Glass
				//documentConversionForFG(file,userid,templateJson);
				newDocumentConversionForFG(file,userid,templateJson);
				try {
					SOP.printSOPSmall("Now responding ...... :: Successful");
					JsonRespond.uploadServletResopnse(logMsg.toString(),
							errorMsg.toString(), response,
							(String) request.getAttribute("fileName"), 200);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				return;
			}
			
			/*  ==================== Now processing non field glass documents ============= */		
			// =============== Threading possible =========================//	
			/* This threading is not possible in case apache tika is on */
			if(uploadServletFirstThread && (!APACHETIKADOCUMENTCONVERSION)){				
				List<UploadServletFirstOPVO> list = UploadServletFirstRunner.run(file);
				for(int i = 0; i < list.size(); i++){
					UploadServletFirstOPVO tempObj = list.get(i);
					if(tempObj.getType().equalsIgnoreCase(UploadServletAnswers)){
						answers = tempObj.getOutput();
					}else if(tempObj.getType().equalsIgnoreCase(UploadServletConvertRes)){
						convertRes = tempObj.getOutput();
					}else{
						continue;
					}
				}	
				
			}else {
				if(APACHETIKADOCUMENTCONVERSION) {
					try {
						doc = ApacheTikaHandler.docConversionDocument(file);
					}catch(Exception e) {
						e.printStackTrace();
					}
					
				}else {
					// Document conversion for static
					answers = DocumentConversionHTMLHandler
							.convertFile(file);
					// Document Conversion for general
					//convertRes = DocumentConversionHandler.convertFile(file);
					convertRes = answers;
				}
				
			}
			
			// ===============================================================//

			// Threading possible
			// ==========================================================================
			// //
						
			if(uploadServletSecondThread){
				List<UploadServletSecondOPVO> list = UploadServletSecondRunner.run(templateFileName, staticTemplateName);
				for(int i = 0; i < list.size(); i++){
					UploadServletSecondOPVO tempObj = list.get(i);
					if(tempObj.getFilename().equalsIgnoreCase(UploadServletSecondThreadTemplateFileName)){
						templateJson = tempObj.getContent();
					}else if(tempObj.getFilename().equalsIgnoreCase(UploadServletSecondThreadStaticTemplateName)){
						staticTemplateJson = tempObj.getContent();
					}else {
						continue;
					}
				}
				
			}else{
				templateJson = SetTemplates(templateFileName);
				staticTemplateJson = SetTemplates(staticTemplateName);
			}
			
			
			PostDocumentConversionHTMLProcessing.classTemplateJson = templateJson;
			PostNLCPropSet.templatefile = Util.getJSONObject(templateJson);
			PostNLCPropSet.staticTemplatefile = Util
					.getJSONObject(staticTemplateJson);
			// ==========================================================================
			// //

			try {
				PostNLCPropSet.templateList = Util.populateTemplateTitle();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(APACHETIKADOCUMENTCONVERSION) {
				if(doc != null) {
					try {
						// For static and dynamic scoring
						JsonAndHTML preProcessedInput = PostDocumentConversionHTMLProcessing
								.ProcessHTML(doc.toString());
						if (preProcessedInput == null) {
							errorMsg.append("Null returned in post Document Conversion.");
						} else {
							// DO the rest of the processing in the following
							// function
							processOnlyHTML(preProcessedInput,file,doc);
							updateDocumentTable(file.getName(), userid);
						}
					}catch (Exception e1) {
						errorMsg.append("Error in Document Conversion or Post processing "
								+ e1.toString());
						e1.printStackTrace();
					}
					
				}else {
					errorMsg.append("Null returned in post Document Conversion.");
				}
			}else {
				if (answers != null) {
					try {
						docHTML = answers.getHtml().toString();
						JsonAndHTML preProcessedInput = PostDocumentConversionHTMLProcessing
								.ProcessHTML(docHTML);
						if (preProcessedInput == null) {
							errorMsg.append("Null returned in post Document Conversion.");
						} else {
							// DO the rest of the processing in the following
							// function
							ProcessInputs(preProcessedInput, convertRes,file);
							updateDocumentTable(file.getName(), userid);
						}
					} catch (Exception e1) {
						errorMsg.append("Error in Document Conversion or Post processing "
								+ e1.toString());
						e1.printStackTrace();
					}

				} else {
					errorMsg.append("Error in Document Conversion");
				}
			}
			

			SOP.printSOPSmall("going from upload");
		}

		SOP.printSOPSmall("Now responding ......");
		
		// response to front end
		try {
			SOP.printSOPSmall("Now responding ...... :: Successful");
			JsonRespond.uploadServletResopnse(logMsg.toString(),
					errorMsg.toString(), response,
					(String) request.getAttribute("fileName"), 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	/*
	 * The following function is for Field Glass. It will do document conversion and then store the data to object Storage.
	 * Now as Static has been included this function will call the FG static method and will get the JSON object in return.
	 * 
	 * 
	 * */
	private void documentConversionForFG(File file, String userid, String templateJson) {
		// TODO Auto-generated method stub
		String docHTML = null;
		boolean found = true;
		ConvertReturn answers = DocumentConversionHTMLHandler
				.convertFile(file);
		if (answers != null) {
			try {
				docHTML = answers.getHtml().toString();
				System.out.println();
				Document doc = Jsoup.parse(docHTML);	
				JSONObject classTemplateJSON = Util.getJSONObject(templateJson);
				JSONObject staticTemplateForFG = FieldGlassStatic.createStaticTemplateFG(doc, classTemplateJSON);
				System.out.println("Bug 1 :: "+staticTemplateForFG);
				handleObjectStoreForJSON(staticTemplateForFG.toJSONString(), file, "STATIC_");	
				System.out.println("Bug 2 :: "+doc.toString());
				handleObjectStoreForHTML(docHTML, file);
				System.out.println("Bug 3 :: "+found);
				updateDocumentTable(file.getName(), userid);
				System.out.println("Bug 4 :: "+found);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			
	}
	
	
	/*
	 * The following function is for Field Glass. It will do document conversion and then store the data to object Storage.
	 * Now as Static has been included this function will call the FG static method and will get the JSON object in return.
	 * 
	 * 
	 * */
	private void newDocumentConversionForFG(File file, String userid, String templateJson) {
		// TODO Auto-generated method stub
		String docHTML = null;
		boolean found = true;
		Document doc = null;
		
		try {
			if(APACHETIKADOCUMENTCONVERSION) {
				/*doc = ApacheTikaHandler.docConversionDocument(file);
				docHTML = ApacheTikaHandler.docConversion(file);			
				doc = ApacheTikaHandler.newApacheTikaDocumentConversion(file);
				docHTML = ApacheTikaHandler.newApacheTikaDocumentConversionString(file);*/
				
				DocumentConversionObject newObj = ApacheTikaHandler.newDcouemntConversionObj(file);
				doc = newObj.getDocumentDocument();
				docHTML = newObj.getDocumentString();
				
			}else {
				ConvertReturn answers = DocumentConversionHTMLHandler
						.convertFile(file);
				if (answers != null) {
						docHTML = answers.getHtml().toString();
						doc = Jsoup.parse(docHTML);	
				}
			}
			JSONObject classTemplateJSON = Util.getJSONObject(templateJson);
			JSONObject staticTemplateForFG = FieldGlassStatic.createStaticTemplateFG(doc, classTemplateJSON);
			System.out.println("Bug 1 :: "+staticTemplateForFG);
			handleObjectStoreForJSON(staticTemplateForFG.toJSONString(), file, "STATIC_");	
			System.out.println("Bug 2 :: "+doc.toString());
			handleObjectStoreForHTML(docHTML, file);
			System.out.println("Bug 3 :: "+found);
			updateDocumentTable(file.getName(), userid);
			System.out.println("Bug 4 :: "+found);			
		}catch(Exception e){
			e.printStackTrace();
		}
				
	}

	/**
	 * 
	 * This function will do the conversion for general scoring. Then split the
	 * general result. Store static, general and dynamic in object storage
	 * @param convertRes 
	 * 
	 * */

	private void ProcessInputs(JsonAndHTML preProcessedInput, ConvertReturn convertRes, File file) {
		// TODO Auto-generated method stub
		JSONObject staticInput = preProcessedInput.getStaticInput();		
		DocumentSplitSentence d = new DocumentSplitSentence();

		JSONObject generalInput = null;
		try {
			generalInput = d.splitMain(Util.getJSONObject(convertRes
					.getAnswer().toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document dynamicInput = preProcessedInput.getDynamicInput();
		

		// ===================== Threading Possible ==================================//
		handleObjectStoreForJSON(staticInput.toJSONString(), file, "STATIC_");
		handleObjectStoreForJSON(generalInput.toJSONString(), file, "GENERAL_");
		handleObjectStoreForHTML(dynamicInput.toString(), file);
		// =========================================================// 
	}
	
	
	/**
	 *  The following function will still have to be implemented
	 * @param doc 
	 * */
	// TODO Commenting and finish coding
	private void processOnlyHTML(JsonAndHTML preProcessedInput, File file, Document doc) {
		
		JSONObject staticInput = preProcessedInput.getStaticInput();		
		DocumentSplitSentence d = new DocumentSplitSentence();

		JSONObject generalInput = null;
		// TODO implementing the general JSONObject input
		try {
			generalInput = PrepareGeneralJSON.HTMLToGeneralJSON(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document dynamicInput = preProcessedInput.getDynamicInput();
		

		// ===================== Threading Possible ==================================//
		handleObjectStoreForJSON(staticInput.toJSONString(), file, "STATIC_");
		handleObjectStoreForJSON(generalInput.toJSONString(), file, "GENERAL_");
		handleObjectStoreForHTML(dynamicInput.toString(), file);
		// =========================================================// 
	}

	

	// This will return the file content as String from object storage
	private String SetTemplates(String fileName) {
		// TODO Auto-generated method stub
		String fileContent = null;
		try {
			fileContent = ObjectStoreHandler.getFileStr(fileName,
					PROPERTIES_CONTAINER);
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileContent;
	}

	/**
	 * The following function will upload the file.
	 * 
	 * @param response
	 * @param request
	 * 
	 * */
	/*private File UploadFile(File file, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		try {
			file = UploadHandler.upload(request, response);
		} catch (Exception e1) {
			errorMsg.append("Error in file upload :: \n " + e1);
			e1.printStackTrace();
		}
		return file;
	}*/

	/**
	 * The following function will set up the workspace ID and also template
	 * file names depending on
	 * 
	 * @param contractType
	 *            :: Which is currently MP & MS. The method returns void
	 */
	private void setUpWorkspaceID(String contractType) {
		// TODO Auto-generated method stub

		if (contractType.equalsIgnoreCase(MPNonFG)) {
			templateFileName = "Template_MP_Classes.json";
			staticTemplateName = "MP_Static_Template_V5.json";
			if (CISCOVCAP) {
				APICredentials.CLASS_WORKSPACE_ID = CiscoVcapUtils.ENV_MP_WORKSPACE_ID;
			} else {
				if (VCAPCONVERSATION) {
					APICredentials.CLASS_WORKSPACE_ID = VcapSetupParser.ENV_MP_WORKSPACE_ID;
				} else {
					APICredentials.CLASS_WORKSPACE_ID = APICredentials.MP_WORKSPACE_ID;
				}
			}

		} else if(contractType.equalsIgnoreCase(MSNonFG)) {
			staticTemplateName = "MS_Static_Template_V5.json";
			templateFileName = "Template_MS_Classes.json";
			if (CISCOVCAP) {
				APICredentials.CLASS_WORKSPACE_ID = CiscoVcapUtils.ENV_MS_WORKSPACE_ID;
			} else {
				if (VCAPCONVERSATION) {
					APICredentials.CLASS_WORKSPACE_ID = VcapSetupParser.ENV_MS_WORKSPACE_ID;
				} else {
					APICredentials.CLASS_WORKSPACE_ID = APICredentials.MS_WORKSPACE_ID;
				}
			}

		} else if(contractType.equalsIgnoreCase(MPFG)) {
			staticTemplateName = "MP_FG_Static_Template_V5.json";
			templateFileName = "Template_MP_FG_Classes.json";
			System.out.println("Contract Type :: "+ MPFG);
		}else {
			staticTemplateName = "MS_FG_Static_Template_V5.json";
			templateFileName = "Template_MS_FG_Classes.json";
			System.out.println("Contract Type :: "+ MSFG);
		}
	}

	private void updateDocumentTable(String fileName, String userId) {
		// TODO Auto-generated method stub
		DocumentsDAO ddao = new DocumentsDAO();
		SOP.printSOPSmall("Calling Insertion");

		ddao.insertIntoDocumentTablePool(fileName, userId, PROCESSING_CONTAINER);
		SOP.printSOPSmall("Done Insertion");
	}

	private void handleObjectStoreForJSON(String string, File file, String type) {
		SOP.printSOPSmall("Inside handleObjectStorage");
		SOP.printSOPSmall("File :: " + file);
		try {
			ObjectStoreHandler.objectStore(
					new ByteArrayInputStream(string.getBytes()),
					Util.replacingFileToJSONExtension(type + file.getName()),
					PROCESSING_CONTAINER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleObjectStoreForHTML(String string, File file) {
		// TODO Auto-generated method stub
		SOP.printSOPSmall("Inside handleObjectStorage");
		SOP.printSOPSmall("File :: " + file);
		try {
			ObjectStoreHandler.objectStore(
					new ByteArrayInputStream(string.getBytes()),
					Util.replacingFileToHTMLExtension(file.getName()),
					PROCESSING_CONTAINER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JSONObject processStrategyArrayList(
			ArrayList<StrategyObject> strategyInfoList) {
		// TODO Auto-generated method stub
		int size = strategyInfoList.size();
		if (size == 0) {
			return null;
		}
		JSONObject finalStructure = new JSONObject();
		String userId = null;
		String strategyName = null;
		String accessType = null;
		String strategyType = null;

		for (int i = 0; i < size; i++) {
			StrategyObject so = strategyInfoList.get(i);
			userId = so.getUserId();
			strategyType = so.getStrategyType();
			accessType = so.getAccessType();
			strategyName = so.getStrategyName();
			if (!finalStructure.containsKey(userId)) {
				JSONObject newKey = new JSONObject();
				JSONObject newValue = new JSONObject();
				JSONArray arr1 = new JSONArray();
				JSONArray arr2 = new JSONArray();
				newValue.put("MP", arr1);
				newValue.put("MS", arr2);
				finalStructure.put(userId, newValue);
			}
			Iterator itr = finalStructure.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next().toString();
				if (key.equalsIgnoreCase(userId)) {
					JSONObject eachObj = (JSONObject) finalStructure.get(key);
					JSONArray arr = (JSONArray) eachObj.get(strategyType);
					JSONObject tempObj = new JSONObject();
					tempObj.put("StrategyName", strategyName);
					tempObj.put("accessType", accessType);
					arr.add(tempObj);
				}
			}
		}
		return finalStructure;
	}
	

	private static JSONObject getStrategyInformationFromDasdDB(String userid,
			String role) {
		// TODO Auto-generated method stub
		JSONObject info = new JSONObject();
		ArrayList<StrategyObject> strategyInfoList = new ArrayList<StrategyObject>();
		if (role.equalsIgnoreCase("U")) {
			strategyInfoList = StrategyDAO.getStartegyInfoForUser();
		} else if (role.equalsIgnoreCase("A")) {
			strategyInfoList = StrategyDAO.getStartegyInfoForAdmin(userid);
		} else {
			System.out.println("Wrong Input given to the function....");
		}
		
		try {
			info = processStrategyArrayList(strategyInfoList);
		}catch(Exception e){
			e.printStackTrace();
		}
				
		return info;
	}
	
	
	
	public static void main(String[] ab){
		String sowFilePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/FG HTML MP/";
		String sowFileName = "ColorCoded_SOW Analyzer MP CSCOTQ00140502(1).pdf_Converted.html";

		// Getting the SOW file(in HTML format)
		File file = new File(sowFilePath + sowFileName);

		// Converting to HTML
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
			// System.out.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Getting the template file
		String classTemplate = "C:/Users/IBM_ADMIN/Documents/Phase2/CISCO-Phase 3/Template_MP_FG_Classes.json";
		JSONObject ClasstemplateJson = null;
		try {
			ClasstemplateJson = Util.getJSONFromFile(classTemplate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject staticTemplateForFG = FieldGlassStatic.createStaticTemplateFG(doc, ClasstemplateJson);
		System.out.println("staticTemplateForFG ::  \n"+staticTemplateForFG);
	}
	
		

}

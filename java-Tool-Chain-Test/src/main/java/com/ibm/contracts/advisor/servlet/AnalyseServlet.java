package com.ibm.contracts.advisor.servlet;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.FieldGlass.Alert.FGAlertHandler;
import com.ibm.contracts.advisor.FieldGlass.DYNAMIC.FieldGlassDynamic;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.dao.implementation.DocumentsDAO;
import com.ibm.contracts.advisor.dynamic.handler.AlertHandler;
import com.ibm.contracts.advisor.dynamic.handler.DynamicClassFunctions;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.threadrunner.AnalyzeServletSecondRunner;
import com.ibm.contracts.advisor.util.CalculateGeneralScore;
import com.ibm.contracts.advisor.util.CiscoPostClassification;
import com.ibm.contracts.advisor.util.CleanDBandObjectStorage;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.StaticScoreCalculator;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.AnalyzeServletSecondOPVO;
import com.ibm.contracts.advisor.vo.FieldGlassOPOBJ;
import com.ibm.contracts.advisor.vo.PostClassiRetPair;

/**
 * The following servlet has many variables. They are as follows,-
 * 
 * 1) dynamicResult: This is a JSON object consists of (key,value) pair as
 * (String, Boolean) and they are (className, True/False) 2)
 * dynamicPopUpContent: This is a JSON object consists of (key,value) pair as
 * (String, String) and they are (className, ContentToShowInClassTree)
 * 
 * */

public class AnalyseServlet extends HttpServlet implements Constants {

	private static JSONObject generalHierarchy, staticHierarchy,
			dynamicHierarchy, generalHelpClasses, staticHelpClasses,
			dynamicHelpClases, dynamicPopUpContent;

	private String standardTemplateName = null;

	private String docConversionStaticResult = null;
	private String docConversionGeneralResult = null;
	private String dynamicProcessingInput = null;
	private String standardTemplate = null;

	private JSONObject docJsonObject_STATIC = null;
	private JSONObject docJsonObject_GENERAL = null;
	private JSONObject templateJsonObject = null;

	private JSONObject dymnamicAlert = null;
	private JSONObject dynamicResult = null;

	private String generalScoreResult = null;
	private JSONObject sendJSON = null;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletContext sc = this.getServletContext();
		StringBuffer errorMsg = new StringBuffer();
		List<String> result = null;

		StringBuffer log = new StringBuffer();

		HttpSession session = SessionHelper.sessionHelp(request, sc, response);

		String jsonObjectName = null;
		String htmlObjectName = null;

		String fileName;
		if (session != null) {
			fileName = request.getParameter("input_file_name");
			String userid = request.getParameter("analyzeUserId");
			String contractType = request.getParameter("contractType");
			String versionNumber = request.getParameter("versionNumber");

			System.out.println("versionNumber = " + versionNumber);
			System.out.println("contractType = " + contractType);

			setStandardTemplate(contractType, versionNumber);

			// Threading possible
			getHelpAndHierarchy(contractType);

			System.out.println("standardTemplateName :: "
					+ standardTemplateName);

			if (fileName == null || userid == null) {
				errorMsg.append("Got null file. Problem in fetching file from session");
			}

			result = getObjectNameFromDB(userid, fileName);

			// Branching for Field Glass. it is written in a separate function
			if (contractType.equalsIgnoreCase(MPFG)
					|| contractType.equalsIgnoreCase(MSFG)) {
				ProcessFieldGlassDocument(result, jsonObjectName,
						htmlObjectName, contractType, fileName,log, errorMsg, response);
				return;
			}

			if (result != null) {
				if (result.size() != 0) {
					jsonObjectName = (String) result.get(0);
					htmlObjectName = getHTMLName(jsonObjectName);
					GetPreprocessedInputFromObjStorage(jsonObjectName,
							htmlObjectName);
				}
				// Getting the standard template
				getStandardTemplate(standardTemplateName);

			} else {
				errorMsg.append("No DB entry for the file under processing in Analyse Servlet");
			}

			double score = 0;
			if (docConversionStaticResult != null || standardTemplate != null) {
				score = callStaticProcessing(fileName);
				System.out
						.println("======= The static SCore is ========= :: \n "
								+ score);
			}

			/* Threading possible */
			if (analyseServletFirstThread) {
				System.out.println("To BE DONE");
			} else {
				// processing dynamic
				processDynamic(contractType);
				// Process General
				processgeneral(contractType, jsonObjectName, userid);
			}

			try {
				// Clearing object storage conditionally
				if (CLEAROBJECTSTORAGE) {
					try {
						CleanDBandObjectStorage.cleanObjectStorage(
								jsonObjectName, htmlObjectName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				JsonRespond.createResponseJSON(
						docJsonObject_STATIC.toJSONString(),
						sendJSON.toJSONString(), log.toString(), score,
						errorMsg.toString(), generalHierarchy.toJSONString(),
						staticHierarchy.toJSONString(),
						generalHelpClasses.toJSONString(),
						staticHelpClasses.toJSONString(),
						dymnamicAlert.toJSONString(),
						dynamicResult.toJSONString(),
						dynamicHierarchy.toJSONString(),
						dynamicHelpClases.toJSONString(),
						dynamicPopUpContent.toJSONString(),
						templateJsonObject.toJSONString(), response, 200);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	
	/**
	 * This is a separate function for Field Glass.
	 * Inputs ::  result -> getting data from dash DB
	 *            jsonObjectName -> the name by which the file is stored in object storage for general
	 *            htmlObjectName -> the name by which the file is stored in object storage for static
	 *            
	 *  It won't return anything but it will respond to the Ajax call with output.
	 * @param fileName 
	 *  
	 * */
	private void ProcessFieldGlassDocument(List<String> result,
			String jsonObjectName, String htmlObjectName, String contractType,
			String fileName, StringBuffer log, StringBuffer errorMsg,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		if (result != null) {
			// / Getting the object name
			jsonObjectName = (String) result.get(0);
			// Converting the json object name to HTML object name
			htmlObjectName = getHTMLName(jsonObjectName);
			getStandardTemplate(standardTemplateName);
			
			try {
				docConversionStaticResult = ObjectStoreHandler.getFileStr("STATIC_"
					+ jsonObjectName, PROCESSING_CONTAINER);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("docConversionStaticResult :: "+ docConversionStaticResult);
			
			//docJsonObject_STATIC = new JSONObject();
			double score = 0;
			try {
				score = callStaticProcessing(fileName);
			}catch (Exception e) {
				e.printStackTrace();
			}


			// Getting the file content from Object storage
			try {
				dynamicProcessingInput = ObjectStoreHandler.getFileStr(
						htmlObjectName, PROCESSING_CONTAINER);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("dynamicProcessingInput :: "+ dynamicProcessingInput);
			
						
			
			dynamicResult = new JSONObject();
			dynamicPopUpContent = new JSONObject();
			
			
			sendJSON = new JSONObject();
			//templateJsonObject = new JSONObject();
			dymnamicAlert = new JSONObject();

			// Sending the file to Field Glass Class. Getting the result for Class
			List<FieldGlassOPOBJ> FGDynamicClassificationResult = FieldGlassDynamic
					.ProcessFieldGlassDynamic(dynamicProcessingInput,
							contractType);
			for (int i = 0; i < FGDynamicClassificationResult.size(); i++) {
				FieldGlassOPOBJ temp = FGDynamicClassificationResult.get(i);
				dynamicResult.put(temp.getClassName(), temp.getFlag());
				dynamicPopUpContent.put(temp.getClassName(), temp.getOutput());
				//dymnamicAlert.put(temp.getClassName(), temp.getOutput());
			}

			// Getting the result for Alert varification
			List<FieldGlassOPOBJ> FGalert = new FGAlertHandler().ProcessAlert(
					dynamicProcessingInput, contractType);
			
			
			for (int i = 0; i < FGalert.size(); i++) {
				FieldGlassOPOBJ temp = FGalert.get(i);
				JSONObject temp1 = new JSONObject();
				temp1.put("flag", temp.getFlag());
				temp1.put("output", temp.getOutput());
				//dymnamicAlert.put(temp.getClassName(), temp.getOutput());
				dymnamicAlert.put(temp.getClassName(), temp1);
			}
			
						
			// Sending back response
			try {
				JsonRespond.createResponseJSON(
						docJsonObject_STATIC.toJSONString(),
						sendJSON.toJSONString(), log.toString(), score,
						errorMsg.toString(), generalHierarchy.toJSONString(),
						staticHierarchy.toJSONString(),
						generalHelpClasses.toJSONString(),
						staticHelpClasses.toJSONString(),
						dymnamicAlert.toJSONString(),
						dynamicResult.toJSONString(),
						dynamicHierarchy.toJSONString(),
						dynamicHelpClases.toJSONString(),
						dynamicPopUpContent.toJSONString(),
						templateJsonObject.toJSONString(), response, 200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 
	 * The following portion of code will run for general score.
	 * 
	 * */
	private void processgeneral(String contractType, String jsonObjectName,
			String userid) {
		// TODO Auto-generated method stub
		try {
			generalScoreResult = CalculateGeneralScore
					.getConversationAPIResult(docConversionGeneralResult,
							jsonObjectName, userid);
			sendJSON = Util.getJSONObject(generalScoreResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PostClassiRetPair b = CiscoPostClassification
					.getMoneyValue(sendJSON);
			System.out.println(b.highestVal);
			System.out.println(b.priceClass);
			sendJSON.put("priceClass", b.priceClass);
			sendJSON.put("highestVal", b.highestVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * The following function will call the dynamic processing.
	 * 
	 * */
	private void processDynamic(String contractType) {
		// TODO Auto-generated method stub
		dynamicPopUpContent = new JSONObject();
		if ((dynamicProcessingInput != null)
				&& (!("").equalsIgnoreCase(dynamicProcessingInput))) {
			dymnamicAlert = AlertHandler.HandleAlerts(dynamicProcessingInput,
					contractType);
			dynamicResult = DynamicClassFunctions.ProcessDynamicClasses(
					dynamicProcessingInput, contractType, dynamicPopUpContent);
		}

		if (dymnamicAlert == null) {
			dymnamicAlert = new JSONObject();
		}
	}

	
	/**
	 * 
	 * Calling static processing part
	 * 
	 * */
	private double callStaticProcessing(String fileName) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();
		double score = 0;

		try {
			docJsonObject_STATIC = (JSONObject) jsonParser
					.parse(docConversionStaticResult);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			templateJsonObject = (JSONObject) jsonParser
					.parse(standardTemplate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("FG templateJsonObject :: "+ templateJsonObject);
		
		score = StaticScoreCalculator.templateMatchFunction(
				docJsonObject_STATIC, templateJsonObject, fileName);

		return score;
	}
	

	/**
	 * Loading standard template
	 * */
	private void getStandardTemplate(String standardTemplateName2) {
		// TODO Auto-generated method stub
		if (!("").equalsIgnoreCase(standardTemplateName)) {
			try {
				standardTemplate = ObjectStoreHandler.getFileStr(
						standardTemplateName, PROPERTIES_CONTAINER);
				PostNLCPropSet.templatefile = Util
						.getJSONObject(standardTemplate);
				System.out.println("standardTemplate FG "+ standardTemplate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * The following code will get all three pre-processed inputs from Object
	 * storage.
	 * */
	private void GetPreprocessedInputFromObjStorage(String jsonObjectName,
			String htmlObjectName) {
		// TODO Auto-generated method stub
		try {
			// System.out.println("=========object storage credential================"+Constants.project);
			docConversionStaticResult = ObjectStoreHandler.getFileStr("STATIC_"
					+ jsonObjectName, PROCESSING_CONTAINER);

			docConversionGeneralResult = ObjectStoreHandler.getFileStr(
					"GENERAL_" + jsonObjectName, PROCESSING_CONTAINER);

			dynamicProcessingInput = ObjectStoreHandler.getFileStr(
					htmlObjectName, PROCESSING_CONTAINER);
			// System.out.println("=========getting json from object storage======"+docConversionResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * The following code will get the object name from database that has been
	 * put there during Document Conversion stage
	 * 
	 * */

	private List<String> getObjectNameFromDB(String userid, String fileName) {
		// TODO Auto-generated method stub
		List<String> result = null;
		DocumentsDAO dao = new DocumentsDAO();
		try {
			// Searching the database to get object storage info
			result = dao.getObjectStorageInfo(userid, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// The following will set the name of the standard template
	private void setStandardTemplate(String contractType, String versionNumber) {
		// TODO Auto-generated method stub
		if (contractType.equalsIgnoreCase(MPNonFG)) {
			standardTemplateName = "MP_Static_Template_V"
					+ versionNumber.toString() + ".json";
		} else if (contractType.equalsIgnoreCase(MSNonFG)) {
			standardTemplateName = "MS_Static_Template_V"
					+ versionNumber.toString() + ".json";
		} else if (contractType.equalsIgnoreCase(MPFG)) {
			standardTemplateName = "MP_FG_Static_Template_V"
					+ versionNumber.toString() + ".json";
		} else {
			standardTemplateName = "MS_FG_Static_Template_V"
					+ versionNumber.toString() + ".json";
		}
	}

	
	/**
	 * 
	 * Depending on contract Type(MP,MS or FG) it will get the corresponding
	 * help classes and hierarchy files and set them up to this classes's
	 * private variable.
	 * 
	 * 
	 * Threading possible
	 * */
	private static void getHelpAndHierarchy(String contractType) {
		// TODO Auto-generated method stub

		// Threading applied
		if (analyseServletSecondThread) {
			List<AnalyzeServletSecondOPVO> list = AnalyzeServletSecondRunner
					.run(contractType);
			for (int i = 0; i < list.size(); i++) {
				AnalyzeServletSecondOPVO tempObj = list.get(i);
				if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletGeneralHierarchy)) {
					generalHierarchy = tempObj.getContent();
				} else if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletStaticHierarchy)) {
					staticHierarchy = tempObj.getContent();
				} else if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletDynamicHierarchy)) {
					dynamicHierarchy = tempObj.getContent();
				} else if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletGeneralHeplClass)) {
					generalHelpClasses = tempObj.getContent();
				} else if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletStaticHelpClass)) {
					staticHelpClasses = tempObj.getContent();
				} else if (tempObj.getTypeOfCall().equalsIgnoreCase(
						analyseServletDynamicHelpClass)) {
					dynamicHelpClases = tempObj.getContent();
				} else {
					continue;
				}
			}

		} else {
			generalHierarchy = getJSONFromObjectStorage(contractType, "GEN",
					"classHierarchy");

			generalHelpClasses = getJSONFromObjectStorage(contractType, "GEN",
					"HelpClasses");

			staticHierarchy = getJSONFromObjectStorage(contractType, "STATIC",
					"classHierarchy");

			staticHelpClasses = getJSONFromObjectStorage(contractType,
					"STATIC", "HelpClasses");

			dynamicHierarchy = getJSONFromObjectStorage(contractType,
					"DYNAMIC", "classHierarchy");

			dynamicHelpClases = getJSONFromObjectStorage(contractType,
					"DYNAMIC", "HelpClasses");
		}

		System.out.println("=========== GOT JSON OBjects. =================");

		if (generalHierarchy != null) {
			System.out.println("generalHierarchy found");
		} else {
			System.out.println("generalHierarchy Not found");
		}

		if (generalHelpClasses != null) {
			System.out.println("generalHelpClasses found");
		} else {
			System.out.println("generalHelpClasses Not found");
		}

		if (staticHierarchy != null) {
			System.out.println("staticHierarchy found");
		} else {
			System.out.println("staticHierarchy Not found");
		}

		if (staticHierarchy != null) {
			System.out.println("staticHierarchy found");
		} else {
			System.out.println("staticHierarchy Not found");
		}

		if (dynamicHierarchy != null) {
			System.out.println("dynamicHierarchy found");
		} else {
			System.out.println("dynamicHierarchy Not found");
		}

		if (dynamicHelpClases != null) {
			System.out.println("dynamicHelpClases found");
		} else {
			System.out.println("dynamicHelpClases Not found");
		}

	}

	
	/*
	 * It will take a JSON object name and change the extension 
	 * to HTML object name. The names are same just the extensions are different.
	 */
	private String getHTMLName(String jsonObjectName) {
		// TODO Auto-generated method stub
		String htmlObjectName = jsonObjectName.substring(0,
				jsonObjectName.lastIndexOf('.') + 1)
				+ "html";
		return htmlObjectName;
	}
	

	/**
	 * The following function will take class hierarchy files from object
	 * storage and assign them to a json object.
	 * 
	 * The variable FGVar denotes that the document is of type FieldGlass
	 * 
	 * For FG we have seperate Hierarchy file but help classes are same.
	 * */
	public static JSONObject getJSONFromObjectStorage(String contractType,
			String scoreType, String objectType) {
		// TODO Auto-generated method stub
		String conType,FGVar="";
				
		if (contractType.equalsIgnoreCase(MPFG)) {
			conType = "MP";
			FGVar = "FG_";
			/*if(objectType.equalsIgnoreCase("HelpClasses")){
				FGVar = "FG_";
			}	*/		
		} else if (contractType.equalsIgnoreCase(MSFG)){
			conType = "MS";
			/*if(objectType.equalsIgnoreCase("HelpClasses")){
				FGVar = "FG_";
			}	*/
			FGVar = "FG_";
		}else if(contractType.equalsIgnoreCase(MPNonFG)){
			conType = "MP";
		}else{
			conType = "MS";
		}
		
		JSONObject hierarchy = new JSONObject();
		String temp = null;
		try {
			temp = ObjectStoreHandler
					.getFileStr(objectType + "CISCO_" + FGVar+scoreType + "_"
							+ conType + ".json", PROPERTIES_CONTAINER);
			hierarchy = Util.getJSONObject(temp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hierarchy;
	}
	
	

	
	/*
	 * The following function will print the Json Object input 
	 * to the fileName received.
	 * */
	private static void printResultJSON(String fileName,
			JSONObject docJsonObject) {
		// TODO Auto-generated method stub

		try (FileWriter file = new FileWriter(fileName + ".json")) {
			file.write(docJsonObject.toJSONString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Main method
	public static void main(String[] ab) {

		// Checking object storage call parallely
		/*
		long startTime = System.nanoTime();

		getHelpAndHierarchy("Managed Projects");

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("TIme taken to Calculate Score :: " + duration);
		*/
		
		
		
		
	}

}

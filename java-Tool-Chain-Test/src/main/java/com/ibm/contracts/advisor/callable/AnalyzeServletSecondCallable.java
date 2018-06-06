package com.ibm.contracts.advisor.callable;

import java.util.concurrent.Callable;

import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.servlet.AnalyseServlet;
import com.ibm.contracts.advisor.vo.AnalyzeServletSecondOPVO;
import com.ibm.contracts.advisor.vo.UploadServletFirstOPVO;
import com.ibm.contracts.advisor.vo.UploadServletSecondOPVO;

public class AnalyzeServletSecondCallable implements Constants, Callable<AnalyzeServletSecondOPVO>{

	// MP,MS or FG
	private String contractType = "";
	
	// GEN, STATIC or DYNAMIC
	private String classType = "";
	
	// Hierarchy or HelpClass
	private String callType = "";
	
	public AnalyzeServletSecondCallable(String contractType, String classType, String callType) {
		// TODO Auto-generated constructor stub
		this.contractType = contractType;
		this.classType = classType;
		this.callType = callType;
	}
	
	public synchronized AnalyzeServletSecondOPVO call() throws Exception { 
		
		AnalyzeServletSecondOPVO outputObj = new AnalyzeServletSecondOPVO();
		JSONObject fileContent = null;		
		
		try {
			fileContent = AnalyseServlet.getJSONFromObjectStorage(contractType, classType,
					callType);
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Setting the content of output Object
		outputObj.setContent(fileContent);	
		
		// Setting the type of call for output object
		if(classType.equalsIgnoreCase("GEN")){
			if(callType.equalsIgnoreCase("classHierarchy")){
				outputObj.setTypeOfCall(analyseServletGeneralHierarchy);
			}else if(callType.equalsIgnoreCase("HelpClasses")){
				outputObj.setTypeOfCall(analyseServletGeneralHeplClass);
			}
		}else if(classType.equalsIgnoreCase("STATIC")){
			if(callType.equalsIgnoreCase("classHierarchy")){
				outputObj.setTypeOfCall(analyseServletStaticHierarchy);
			}else if(callType.equalsIgnoreCase("HelpClasses")){
				outputObj.setTypeOfCall(analyseServletStaticHelpClass);
			}
		}else if(classType.equalsIgnoreCase("DYNAMIC")){
			if(callType.equalsIgnoreCase("classHierarchy")){
				outputObj.setTypeOfCall(analyseServletDynamicHierarchy);
			}else if(callType.equalsIgnoreCase("HelpClasses")){
				outputObj.setTypeOfCall(analyseServletDynamicHelpClass);
			}
		}else {
			outputObj.setTypeOfCall("");
		}
		
		return outputObj;
	}
}

package com.ibm.contracts.advisor.callable;

import java.util.concurrent.Callable;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.vo.UploadServletSecondOPVO;

public class UploadServletSecondCallable implements Constants, Callable<UploadServletSecondOPVO>{
	private String fileType = "";
	private String fileName = "";
	
	/*public UploadServletSecondCallable(String fileName) {
		// TODO Auto-generated constructor stub
		if(fileName.equalsIgnoreCase(UploadServletSecondThreadTemplateFileName)){
			this.templateFileName = templateFileName;		
		}else {
			this.staticTempleateName = staticTempleateName;
		}			
	}*/
	
	public UploadServletSecondCallable(String fileType, String fileName) {
		// TODO Auto-generated constructor stub
		this.fileType = fileType;
		this.fileName = fileName;
	}

	
	
	public synchronized UploadServletSecondOPVO call() throws Exception { 
		
		UploadServletSecondOPVO outputObj = new UploadServletSecondOPVO();
		String fileContent = "";		
		
		try {
			fileContent = ObjectStoreHandler.getFileStr(fileName,
					PROPERTIES_CONTAINER);
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputObj.setFilename(fileType);		
		
				
		outputObj.setContent(fileContent);		
		return outputObj;
	}

}

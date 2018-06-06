package com.ibm.contracts.advisor.callable;

import java.util.concurrent.Callable;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.handler.DocumentConversionHandler;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.UploadServletFirstOPVO;
import com.ibm.contracts.advisor.vo.UploadServletFirstVO;

public class UploadServletFirstCallable implements Constants, Callable<UploadServletFirstOPVO>{
	
	private UploadServletFirstVO inputVO;
	
	public UploadServletFirstCallable(UploadServletFirstVO inputVO) {
		// TODO Auto-generated constructor stub
		this.inputVO = inputVO;
	}

	
	public synchronized UploadServletFirstOPVO call() throws Exception {
		
		if (inputVO.getType().equalsIgnoreCase(UploadServletAnswers)) {
						
			UploadServletFirstOPVO outputVO = new UploadServletFirstOPVO();
			ConvertReturn answers = DocumentConversionHTMLHandler.convertFile(inputVO.getFile());			
			outputVO.setOutput(answers);
			outputVO.setType(UploadServletAnswers);
			return outputVO;
		}
		
		if (inputVO.getType().equalsIgnoreCase(UploadServletConvertRes)) {
			UploadServletFirstOPVO outputVO = new UploadServletFirstOPVO();			
			ConvertReturn convertRes = DocumentConversionHandler.convertFile(inputVO.getFile());				
			outputVO.setOutput(convertRes);
			outputVO.setType(UploadServletConvertRes);		
			
			return outputVO;
		}
		
		return null;
	}
	
	
}

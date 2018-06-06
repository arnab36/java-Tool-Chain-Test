package com.ibm.contracts.advisor.vo;

import java.io.File;

public class AnalyzeServletSecondVO {

	// MP,MS or FG
	private String contractType = "";

	// GEN, STATIC or DYNAMIC
	private String classType = "";

	// Hierarchy or HelpClass
	private String callType = "";

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

}

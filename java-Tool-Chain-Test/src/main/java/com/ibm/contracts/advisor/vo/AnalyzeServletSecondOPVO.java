package com.ibm.contracts.advisor.vo;

import org.json.simple.JSONObject;

public class AnalyzeServletSecondOPVO {
	
	private JSONObject content;
	private String typeOfCall;
	
	public JSONObject getContent() {
		return content;
	}
	public void setContent(JSONObject content) {
		this.content = content;
	}
	
	public String getTypeOfCall() {
		return typeOfCall;
	}
	public void setTypeOfCall(String typeOfCall) {
		this.typeOfCall = typeOfCall;
	}
	
	
}

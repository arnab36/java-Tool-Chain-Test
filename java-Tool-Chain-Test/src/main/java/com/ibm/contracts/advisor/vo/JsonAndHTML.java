package com.ibm.contracts.advisor.vo;

import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;


/**
 * 
 * The following class will return the combined object for static and dynamic pre-processed input
 * */

public class JsonAndHTML {
	
	private JSONObject staticInput = null;
	private Document dynamicInput = null;
	private JSONObject generalInput = null;
	
	public void setStaticInput(JSONObject staticInput) {
		this.staticInput = staticInput;
	}
	
	public void setDunamicInput(Document dynamicInput) {
		this.dynamicInput = dynamicInput;
	}
	
	public void setGeneralInput(JSONObject generalInput) {
		this.generalInput = generalInput;
	}
	
	public JSONObject getStaticInput(){
		return this.staticInput;
	}
	
	public Document getDynamicInput(){
		return this.dynamicInput;
	}
	
	public JSONObject getGeneralInput(){
		return this.generalInput;
	}
}
package com.ibm.contracts.advisor.vo;

import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.util.Util;

public class ClassificationVO {
private int threadNo;
private JSONObject result;
private boolean executed;

public boolean isExecuted() {
	return executed;
}

public int getThreadNo() {
	return threadNo;
}

public JSONObject getResult() {
	return result;
}

public ClassificationVO(int threadNo, String result,boolean executed) {
	this.result =Util.getJSONObject(result);
	this.threadNo = threadNo;
	this.executed=executed;
	
}

}

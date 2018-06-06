package com.ibm.contracts.advisor.vo;

import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;

public class ConvertReturn {
	 public Answers getAnswer() {
		return answer;
	}

	public void setAnswer(Answers answer) {
		this.answer = answer;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	Answers answer=null;
	String error=null;
	String log=null;
	String html=null;
	String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
	
	public ConvertReturn(Answers answer, String error, String log) {
		super();
		this.answer = answer;
		this.error = error;
		this.log = log;
	}
	
	
}

package com.ibm.contracts.advisor.vo;

import com.ibm.json.java.JSONObject;

public class IndexSentencePair { 
	private int matchIndex;
	private int totalStaticSentences;
	private JSONObject ansUnit;
	
	public void setMatchIndex(int matchIndex) {
		this.matchIndex = matchIndex;
	}
	
	public void setTotalStaticSentences(int totalStaticSentences) {
		this.totalStaticSentences = totalStaticSentences;
	}
	
	public void setJSON(JSONObject ansUnit) {
		this.ansUnit = ansUnit;
	}
	
	public int getMatchIndex(){
		return this.matchIndex;
	}
	
	public int getTotalStaticSentences(){
		return this.totalStaticSentences;
	}
	
	public JSONObject getAnsUnit(){
		return this.ansUnit;
	}
	
}
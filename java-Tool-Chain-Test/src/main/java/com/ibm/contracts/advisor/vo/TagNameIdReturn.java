package com.ibm.contracts.advisor.vo;



public class TagNameIdReturn{
	
	private String tagType = null;
	private String tagName = null;
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	
	public String getTagName(){
		return this.tagName;
	}
	
	public String getTagType(){
		return this.tagType;
	}
}
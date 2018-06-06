package com.ibm.contracts.advisor.vo;


public class ParagraphObject{
	
	private String HtagName = null;
	private String HtagID = null;
	private String PtagString = null;
	private String TtagString = null;
	
	public void setHtagName(String HtagName) {
		this.HtagName = HtagName;
	}
	
	public void setHtagID(String HtagID) {
		this.HtagID = HtagID;
	}
	
	public void setPtagString(String PtagString) {
		this.PtagString = PtagString;
	}
	
	public void setTtagString(String TtagString) {
		this.TtagString = TtagString;
	}
	
	public String getHtagName(){
		return this.HtagName;
	}
	
	public String getHtagID(){
		return this.HtagID;
	}
	
	public String getPtagString(){
		return this.PtagString;
	}
	
	public String getTtagString(){
		return this.TtagString;
	}
}




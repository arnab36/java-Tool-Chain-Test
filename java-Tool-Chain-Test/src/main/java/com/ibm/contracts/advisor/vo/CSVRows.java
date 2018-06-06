package com.ibm.contracts.advisor.vo;

public class CSVRows {

    private String template_class;
    private int title_match;
    private float content_match;

    public void setRows(String template_class, int title_match,  float content_match) {
        this.template_class = template_class;
        this.title_match = title_match;
        this.content_match = content_match;
    }
    
    public String getTemplate_class(){
    	return this.template_class;
    }
    
    public int getTitle_match(){
    	return this.title_match;
    }
    
    public float getContent_match(){
    	return this.content_match;
    }

  
}
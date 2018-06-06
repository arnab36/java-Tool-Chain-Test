package com.ibm.contracts.advisor.vo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentConversionObject {
	
	private String documentString;
	private Document documentDocument;
	
	public DocumentConversionObject(String output) {
		this.documentString = output;
		this.documentDocument = Jsoup.parse(output, "UTF-8");	   
	}
	
	public String getDocumentString() {
		return documentString;
	}
	
	public void setDocumentString(String documentString) {
		this.documentString = documentString;
	}
	
	public Document getDocumentDocument() {
		return documentDocument;
	}
	
	public void setDocumentDocument(Document documentDocument) {
		this.documentDocument = documentDocument;
	}
	
	
	
}

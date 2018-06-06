package com.ibm.contracts.advisor.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.handler.DocumentConversionHTMLHandler;
import com.ibm.contracts.advisor.util.PostDocumentConversionHTMLProcessing;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ConvertReturn;

	/**
	 *   This class is specially made for Ayan.
	 *   It will take a document and convert it into HTML
	 *   Then it will take that HTML and process and Assign ID to it(CISCO PostDocumentConversionHTMLProcessing).
	 *   Then it will remove the static part and write it to disk
	 * */

public class PrepareDynamicDocument {

	public static void main(String[] args) throws FileNotFoundException,
	IOException, InterruptedException, ExecutionException,
	ParseException{
		// TODO Auto-generated method stub
		String outputFilePath = "C:/Users/IBM_ADMIN/Documents/testnow/FieldGlass/Defect Document HTML/";
		String fileName = "CSCOTQ00142089.pdf";
		File file = new File(
				"C:/Users/IBM_ADMIN/Documents/testnow/cisco-samples/"+fileName);
		// System.out.println(file);

		JSONObject ClasstemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/Template_MS_Classes.json");

		JSONObject MP_TemplateJson = Util
				.getJSONFromFile("C:/CISCO-v2-MP/config/MS_Static_Template_V5.json");

		PostNLCPropSet.templatefile = Util.getJSONObject(ClasstemplateJson
				.toJSONString());
		
		// Document conversion
		ConvertReturn answers = DocumentConversionHTMLHandler
				.convertFile(file);			
		
		String docHTML = null;

		docHTML = answers.getHtml().toString();
		Document doc = Jsoup.parse(docHTML);	
		
		
		Document dynamicDoc = PostDocumentConversionHTMLProcessing.processOLTag(doc);
		dynamicDoc = PostDocumentConversionHTMLProcessing.ProcessHTMLAndAssignTagID(dynamicDoc, ClasstemplateJson);
		
		dynamicDoc = RemoveStatic.removeAllStatic(
				MP_TemplateJson, dynamicDoc.toString());
		
		docConvert_V4.WriteToFile(dynamicDoc,file.getName(),outputFilePath);
		
		
	}
	
	

}

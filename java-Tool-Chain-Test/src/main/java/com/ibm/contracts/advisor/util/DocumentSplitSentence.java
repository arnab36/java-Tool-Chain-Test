package com.ibm.contracts.advisor.util;

/**
 * 
 */


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;


import com.ibm.contracts.advisor.constants.SplitPropSet;
import com.ibm.contracts.advisor.util.Util;


/**
 * @author Atrijit
 * 
 */
public class DocumentSplitSentence {

	/**
	 * 
	 */


	/**
	 * @param content : the text that will be splitted
	 * @param htmlConverted 
	 * @return the list of splitted sentence
	 */
	private List split(String content) {// equivalent of Split_Section_Content
		//System.out.println("before :::::::"+content);
		content = content.replaceAll("[^\\x00-\\x7F]", " "); //
		//System.out.println("after :::::::"+content);
		List singleText = new LinkedList();
		System.out.println("*************************************8");
		if (Util.wordCount(SplitPropSet.patternWords, content) > 10) {
			List secTextList = segmentSentences(content);
			for(int k=0;k<secTextList.size();k++){
				System.out.println(secTextList.get(k));
			}
			return secTextList;

		} else {
			singleText.add(content);
			return singleText;
		}
		//System.out.println("*************************************8");

	}

	

	private List segmentSentences(String content) {
		List secTextList = new LinkedList();
		String[] sentSet = Util.splitToSent(SplitPropSet.patternSplitSentence,
				content);
		for (int i = 0; i < sentSet.length; i++) {
			int textCount = Util.wordCount(SplitPropSet.patternWords, sentSet[i]);
			if (textCount >= 4) {
				if (textCount < SplitPropSet.DWORDCOUNT) {
					// System.out.println(sentSet[i]);
					//System.out.println("==========================================");
					secTextList.add(sentSet[i]);
	
					}
					else{
						secTextList.add(sentSet[i]);
					}
				} else{
					
						secTextList.add(sentSet[i]);
					
					
				}
					
			}
		

		// TODO Auto-generated method stub
		return secTextList;
	}



	/**
	 * @param htmlConverted 
	 * @param args
	 */

	public JSONObject splitMain(JSONObject docConvertJson) throws IOException, ParseException {
		List result = null;
		
		JSONArray docConvertJsonContent = (JSONArray) docConvertJson
				.get("answer_units");// get the answer units array from json

		int size = docConvertJsonContent.size();
		for (int i = 0; i < size; i++) {
			String content = (String) ((JSONObject) ((JSONArray) ((JSONObject) docConvertJsonContent
					.get(i)).get("content")).get(0)).get("text");// from that
																	// array get
			System.out.println(content);
			System.out.println("======****************=============================");
			result = new DocumentSplitSentence().split(content);
			JSONArray ja = new JSONArray();
			for (int j = 0; j < result.size(); j++) {
				HashMap m = new HashMap();
				m.put("media_type", "text/plain");
				m.put("text", result.get(j));
				ja.add(m);
				//System.out.println(ja.toString());
				//System.out.println("=============================================");
				((JSONObject) docConvertJsonContent.get(i)).put("content", ja);
			}
		}
		//System.out.println(docConvertJson.toString());
		SOP.printSOP(docConvertJson.toString());
		/*FileWriter jsonFile = new FileWriter(SplitPropSet.saveAfterSplitSent);
		jsonFile.write(docConvertJson.toJSONString());
		jsonFile.close();*/
		System.out.println("Successfully Copied JSON Object to File...");
		// System.out.println("\nJSON Object: " + obj);

		return docConvertJson;
	}

}

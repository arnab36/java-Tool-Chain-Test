package com.ibm.contracts.advisor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;

public class PostClassifyCleaner implements Constants{
private static JSONObject titleClass;

/**
 * Post NLC cleaner IBM.
 *
 * @param list :it will contain the text number as key and result of the text from NLC
 * @param jsonfilestr :
 * @return the string
 * @throws FileNotFoundException the file not found exception
 * @throws IOException Signals that an I/O exception has occurred.
 * @throws ParseException the parse exception
 */
public static String PostClassifyCleanerMain(Map<Integer, JSONObject> list, String jsonfilestr) throws FileNotFoundException, IOException, ParseException{
	JSONParser parser = new JSONParser();
	//System.out.println(jsonfilestr);
	JSONObject splitedJSON=Util.getJSONObject(jsonfilestr);
	boolean chargesTM=false;
	//String errTxt="";
	boolean fp=false;	
	JSONArray docConvertJsonContent = (JSONArray) splitedJSON
			.get("answer_units");// get the answer units array from json
	int counter=0;
	int size = docConvertJsonContent.size();
	
	int cntr=0;
	for (int i = 0; i < size; i++) {
		
		String titleText = (String)  ((JSONObject) docConvertJsonContent
				.get(i)).get("title");
		
		
		titleText = titleText.replaceAll("[^\\x00-\\x7F]", "");
		titleText=titleText.replaceAll(PostNLCPropSet.patternComp, "COMPANY");
		titleText=titleText.replaceAll(PostNLCPropSet.patternSup, "SUPPLIER");
		((JSONObject) ((JSONArray) ((JSONObject) docConvertJsonContent
				.get(i)).get("content")).get(0)).put("title", titleText);
		JSONArray content =  (JSONArray) ((JSONObject) docConvertJsonContent
				.get(i)).get("content");
		JSONArray tmpContent=new JSONArray();
		for(int j=0;j<content.size();j++){
			//System.out.println(list.get(i).toString());
			JSONObject tmpContentPart=new JSONObject();
			String text=((JSONObject)content.get(j)).get("text").toString();
			if(i==0) //means main title
			{
				//double confScore=searchTitle(PostNLCPropSet.titleQuery,titleText);
				double confScore=((float)FuzzySearch.tokenSetRatio(PostNLCPropSet.titleQuery,titleText)+Constants.fuzzeThreshold)/100.0;
				HashMap<String, String> titleClassConv =(HashMap<String, String>) ((HashMap<String, String>)PostNLCPropSet.getTitleClass()).clone();
				
				if(confScore>0.5){
					titleClassConv.put("confidence",Double.toString(confScore) );
					
				}
				else{
					titleClassConv.put("CLASS", "BLANK");
					titleClassConv.put("confidence",Double.toString(0.0) );
					
				}
				 ((JSONObject) docConvertJsonContent.get(i)).putAll(titleClassConv);
				
				
			}
			else if(((JSONObject) docConvertJsonContent.get(i)).get("parent_id").equals("")){
				JSONObject secTitlesClassify=Util.searchSectionTitle(titleText);
				
				((JSONObject) docConvertJsonContent.get(i)).putAll(secTitlesClassify);
			}
			HashMap<String, String> resultSet=null;
			
				 resultSet=nlcClassificationTextContentLessWC(list.get(cntr),text);
			cntr++;
			
			double conf=0.0;
			String topClassName=resultSet.get("CLASS");
			try{
			 conf=Double.parseDouble(resultSet.get("confidence"));
			}
			catch(Exception e){
			//	System.out.println("::::::::::::::::::"+resultSet.get("confidence").toString());
				System.out.println("except bouble ::"+text);
				e.printStackTrace();
			}
			
			if(topClassName.equalsIgnoreCase("TMCharges") ||topClassName.equalsIgnoreCase("TMRateTable")||topClassName.equalsIgnoreCase("EstimatedChargesOnly"))
			{
				if(conf>=0.7)
					chargesTM=true;
			}
			if(topClassName.equalsIgnoreCase("FixedCharges") ||topClassName.equalsIgnoreCase("FixedMonthlyCharges")){
				if(conf>=0.7)
					fp=true;
			} 
			
			tmpContentPart.putAll(resultSet);
			
			tmpContent.add(tmpContentPart);
		}
		
		//System.out.println(tmpContent);
		//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		((JSONObject) docConvertJsonContent.get(i)).put("content", tmpContent);
		
	}
	
	//System.out.println(docConvertJsonContent);
	splitedJSON.put("answer_units", docConvertJsonContent);
	if(chargesTM==true)
		splitedJSON.put("chargesTM", "T");
	else
		splitedJSON.put("chargesTM", "F");

	if(fp==true)
		splitedJSON.put("fp", "T");
	else
		splitedJSON.put("fp", "F"); 
	System.out.println("chargesTM:::::::::::"+chargesTM);
	System.out.println("fp:::::::::::"+fp);
	return splitedJSON.toJSONString();
	
}

/**
 * Nlc classification text content.
 *it will create a hashmap that will contain the class, its confidence level and class 
 *alternatives from the result of NLC
 * @param nlcResult : the result we got from NLC for the text content
 * @param content :see previous paramter
 * @return the hash map
 */


private static HashMap<String, String> nlcClassificationTextContentLessWC(JSONObject nlcResult, String content) {
	/*System.out.println("****************************************");
	System.out.println(nlcResult.get("text"));
	System.out.println(content);
	System.out.println("****************************************");*/
	int sentLen=Util.wordCount(patternWords, content);
	//HashMap<String, String> secContentClass =(HashMap<String, String>) ((HashMap<String, String>)PostNLCPropSet.getTitleClass()).clone();
	JSONObject secContentClass=new JSONObject();
	//secContentClass.put("fp", "f");
	
		if(sentLen>10){
			try{
			JSONObject topClass = (JSONObject) ((JSONArray)nlcResult.get("classes")).get(0);
			secContentClass.put("CLASS", (String) topClass.get("class_name"));
			secContentClass=CiscoPostClassification.changeForTopClass(topClass,nlcResult,secContentClass);
			
			
			 ((JSONArray)nlcResult.get("classes")).remove(0);
			secContentClass.put("class_alternative",(JSONArray) nlcResult.get("classes"));
			
			secContentClass.put("Quality", "1");
			}
			catch(Exception ne){
				ne.printStackTrace();
				
				secContentClass.put("CLASS", "BLANK");
				secContentClass.put("confidence", "0.0");
				secContentClass.put("Quality", "0");
				secContentClass.put("class_alternative", "");
			}
			
		}
		else if(sentLen==0){
			secContentClass.put("CLASS", "BLANK");
			secContentClass.put("confidence", "0.0");
			secContentClass.put("Quality", "0");
			secContentClass.put("class_alternative", "");
			
		}
		else{
			secContentClass.put("CLASS", "Header");
			secContentClass.put("confidence", "0.0");
			secContentClass.put("Quality", "0");
			secContentClass.put("class_alternative", "");
		}
	
	
	secContentClass.put("text", content);
	return secContentClass;
}




private static HashMap<String, String> nlcClassificationTextContentLessWC111111111(JSONObject nlcResult, String content) {
	
	int sentLen=Util.wordCount(patternWords, content);
	//HashMap<String, String> secContentClass =(HashMap<String, String>) ((HashMap<String, String>)PostNLCPropSet.getTitleClass()).clone();
	JSONObject secContentClass=new JSONObject();
	//secContentClass.put("fp", "f");
	
		if(sentLen>10){
			try{
			JSONObject topClass = (JSONObject) ((JSONArray)nlcResult.get("classes")).get(0);
			secContentClass.put("CLASS", (String) topClass.get("class_name"));
			secContentClass=CiscoPostClassification.changeForTopClass(topClass,nlcResult,secContentClass);
			
			
			 ((JSONArray)nlcResult.get("classes")).remove(0);
			secContentClass.put("class_alternative",(JSONArray) nlcResult.get("classes"));
			
			secContentClass.put("Quality", "1");
			}
			catch(Exception ne){
				System.out.println(ne);
				/*secContentClass.put("CLASS", "BLANK");
				secContentClass.put("confidence", "0.0");
				secContentClass.put("Quality", "0");
				secContentClass.put("class_alternative", "");*/
				JSONObject topClass = (JSONObject) ((JSONArray)nlcResult.get("classes")).get(0);
				secContentClass.put("CLASS", (String) topClass.get("class_name"));
				secContentClass.put("confidence",topClass.get("confidence"));
				 ((JSONArray)nlcResult.get("classes")).remove(0);
				secContentClass.put("class_alternative",(JSONArray) nlcResult.get("classes"));
				secContentClass.put("Quality", "1"); 
			}
			
		}
		else if(sentLen==0){
			secContentClass.put("CLASS", "BLANK");
			secContentClass.put("confidence", "0.0");
			secContentClass.put("Quality", "0");
			secContentClass.put("class_alternative", "");
			
		}
		else{
			secContentClass.put("CLASS", "Header");
			secContentClass.put("confidence", "0.0");
			secContentClass.put("Quality", "0");
			secContentClass.put("class_alternative", "");
		}
	
	
	secContentClass.put("text", content);
	return secContentClass;
}


}


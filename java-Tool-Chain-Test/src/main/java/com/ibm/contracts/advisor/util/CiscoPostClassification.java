package com.ibm.contracts.advisor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.vo.PostClassiRetPair;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class CiscoPostClassification implements Constants{
	
	/**
	 * Find all.
	 *
	 * @param text :where the pattern will be searched
	 * @param pattern : needed for search
	 * @return the linked list
	 */
	private static LinkedList<String> findAll(String text,Pattern pattern){
		LinkedList<String> res=new LinkedList<String>();
		Matcher matcher = pattern.matcher(text) ;
		while (matcher.find()) {
		   
		    res.add(matcher.group());
		}
		return res;
	}
	
	
	/**
	 * Gets the money value: money value /contract price extraction from text
	 *
	 * @param classifyResult the classification result
	 * @return the money value
	 */
	public static PostClassiRetPair getMoneyValue(JSONObject classifyResult){
		PostNLCPropSet.intiMoneyClass();
		String contentString="";
		JSONObject copiedJSON=(JSONObject) classifyResult.clone();
		JSONArray ansUnit=(JSONArray) copiedJSON.get("answer_units");
		for(int i=0;i<ansUnit.size();i++){
			JSONArray contents=(JSONArray)((JSONObject)ansUnit.get(i)).get("content");
			for(int j=0;j<contents.size();j++){
				//System.out.println((String)((JSONObject)contents.get(j)).get("CLASS"));
				boolean moneyClass=isMoneyClass((String)((JSONObject)contents.get(j)).get("CLASS"));
				
				if(moneyClass==true){
					
					contentString=contentString+" "+(String)((JSONObject)contents.get(j)).get("text");
					contentString=contentString+" "+(String)((JSONObject)ansUnit.get(i)).get("title");
				}
			}
			
		}
		//System.out.println(contentString.length());
		LinkedList<String> euroList = findAll(contentString,PostNLCPropSet.patternEuro);
		LinkedList<String> usList = findAll(contentString,PostNLCPropSet.patternUs);
		LinkedList<String> canadaList = findAll(contentString,PostNLCPropSet.patternCanada);
		
		LinkedList<Float> finalList =new LinkedList<Float>();
		finalList=gatherMoneyVal(finalList,usList,"us");
		finalList=gatherMoneyVal(finalList,euroList,"euro");
		finalList=gatherMoneyVal(finalList,canadaList,"canada");
		
		float highestVal=0;
		for(Float f:finalList){
		//	System.out.println(" money value ::::"+f);
			if(f>highestVal)
				highestVal=f;
		}
		if(highestVal==0)
			highestVal=2000;
		
		int TMclassCount = findAll(contentString,PostNLCPropSet.regTM).size();
		
		int FPclassCount = findAll(contentString,PostNLCPropSet.regFP).size();
		String Price_class=null;
	    if( TMclassCount > FPclassCount)
	        Price_class = "Time and Materials";
	    else if( TMclassCount > FPclassCount)
	        Price_class = "Fixed Price";
	    else
	        Price_class = "Fixed Price";

	    PostClassiRetPair pc=new PostClassiRetPair(Price_class,highestVal);
		return pc;
	}

	


	/**
	 * Checks if className belongs to money class is money class.
	 *
	 * @param className the class name
	 * @return true, if is money class
	 */
	private static boolean isMoneyClass(String className) {
		for(String s:PostNLCPropSet.moneyClass){
			if(s.equalsIgnoreCase(className))
				return true;
		}
		return false;
	}
	
	

	/**
	 * Gather money val: gather all the money value into list
	 *
	 * @param finalList the final list
	 * @param currency the currency
	 * @param country the country
	 */
	private static LinkedList<Float> gatherMoneyVal(LinkedList<Float> finalList,LinkedList<String> currency,
			String country){
		for(String c:currency){
			String tmp=c;
			if(country.equalsIgnoreCase("euro"))
				tmp=tmp.replaceAll(".", "");
			if(country.equalsIgnoreCase("canada"))
				tmp=tmp.trim();
			
			 tmp=tmp.replaceAll(",", "");
			 float b=0;
			 try{
			 b=Float.parseFloat(tmp);
			 }
			 catch(Exception e){
				 //System.out.println(e);
			 }
			finalList.add(b);
		}
		return finalList;
		
	}
	
	public static void main1(String[] args) throws FileNotFoundException, IOException, ParseException {
		/*PostNLCPropSet.intiMoneyClass();
		Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
		findAll("1221.432.32",patternEuro);*/
		PostNLCPropSet.intiMoneyClass();
		JSONObject a=Util.getJSONFromFile("C:/Users/IBM_ADMIN/Documents/project 6/json samples/Cisco/nlcToConv.json");
		PostClassiRetPair b = getMoneyValue(a);
		System.out.println(b.highestVal);
		System.out.println(b.priceClass);
	}

	/**
	 * Gets result  from conversation that will return class 1 class 2 and class 3: 
	 *
	 * @param sentence the sentence
	 * @return the from conv
	 */
	private static JSONObject getFromConv(String sentence){
		MessageRequest newMessage = new MessageRequest.Builder().alternateIntents(true).inputText(sentence).build();
		MessageResponse response = null;
		ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
		service.setUsernameAndPassword(APICredentials.CONVERSATION_USERNAME, APICredentials.CONVERSATION_PASSWORD);
		response = service.message(APICredentials.workspace_id_NLC_DEL_V1_Cisco, newMessage).execute();
		
		return Util.getJSONObject(response.toString());
	}

	/**
	 * Change for top class:it will change the top class and confidence based on some condition if the top class is 
	 * deliverable or FPorTMTerms 
	 *
	 * @param topClass: the top class
	 * @param nlcResult: the classification result
	 * @param secContentClass :where the 
	 * @return the JSON object
	 */
	public static JSONObject changeForTopClass(JSONObject topClass, JSONObject nlcResult, JSONObject secContentClass) {
		String topClassName=(String) topClass.get("class_name");
		double conf=Double.parseDouble(topClass.get("confidence").toString());
		
		String sentence=nlcResult.get("text").toString();
		
		/*if(topClassName.equalsIgnoreCase("Deliverables")){
			
			JSONObject convRes=getFromConv(sentence);
			String dtopclass =(String)((JSONObject) ((JSONArray)convRes.get("intents")).get(0)).get("intent");
			Double dclassifiedLabelConf=Double.parseDouble(((JSONObject) ((JSONArray)convRes.get("intents")).get(0)).get("confidence").toString());
			double finalConf=dclassifiedLabelConf*conf;
			secContentClass.put("confidence", Double.toString(finalConf));
			secContentClass.put("Quality", dtopclass);
		
		}*/
		 if(topClassName.equalsIgnoreCase("FPorTMTerms")){
			
			int TMclassCount = findAll(sentence,PostNLCPropSet.regTM).size();
			int FPclassCount = findAll(sentence,PostNLCPropSet.regFP).size();
			
			if(TMclassCount > FPclassCount && TMclassCount > 0){
				secContentClass.put("CLASS", "TMTerms");
				secContentClass.put("confidence", Double.toString(conf));
			}
			else if(TMclassCount < FPclassCount && FPclassCount > 0){
				secContentClass.put("CLASS", "FPTerms");
				secContentClass.put("confidence", Double.toString(conf));
			}
			else{
				secContentClass.put("CLASS", "FPTerms");
				secContentClass.put("confidence", "0.25");
			}
			secContentClass.put("Quality", "1");
			
			
                
			
		}
		else{
			secContentClass.put("confidence", Double.toString(conf));
			secContentClass.put("Quality", "1");
		}
		
		return secContentClass;
		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		/*PostNLCPropSet.intiMoneyClass();
		Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
		findAll("1221.432.32",patternEuro);*/
		PostNLCPropSet.intiMoneyClass();
		JSONObject a=Util.getJSONFromFile("C:/Users/IBM_ADMIN/My Documents/SametimeFileTransfers/Digital Reinvention FedEx SOW April 24 2017.docx_final.json");
		PostClassiRetPair b = getMoneyValue(a);
		System.out.println(b.highestVal);
		System.out.println(b.priceClass);
	}

}

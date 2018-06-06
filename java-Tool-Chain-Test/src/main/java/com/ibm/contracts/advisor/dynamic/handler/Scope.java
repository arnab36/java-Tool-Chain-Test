package com.ibm.contracts.advisor.dynamic.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.SplitPropSet;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
public class Scope implements Constants {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static boolean getScope(String s,
			JSONObject dynamicPopUpContent) {
		 
		
		String CONVERSATION_USERNAME = null;
		String CONVERSATION_PASSWORD =null;
		String CONVERSATION_URL = null;
		
		if(CISCOVCAP){
			//CiscoVcapUtils.getConversationCredentials(CiscoVcapUtils.VCAPUtils());
			 CONVERSATION_USERNAME = CiscoVcapUtils.convUsername;
			 CONVERSATION_PASSWORD = CiscoVcapUtils.convPassword;
			 CONVERSATION_URL = CiscoVcapUtils.ENV_CONVERSATION_URL;
		}else{
			if (VCAPCONVERSATION) {
				
				/*VcapSetupParser.setConversationCredentials(VcapSetupParser
						.VCAPUtils());
				VcapSetupParser.setEnvironmentVariable();
				*/
				
				CONVERSATION_USERNAME = VcapSetupParser.VCAPCONVERSATION_userID;
				CONVERSATION_PASSWORD = VcapSetupParser.VCAPCONVERSATION_password;
				CONVERSATION_URL = VcapSetupParser.ENV_SCOPE_CONVERSATION_WORKSPACEID;
			} else {
				
				/*CONVERSATION_USERNAME = "eb88d8fc-94d6-4f0d-a2c3-83ed3347102b";
				CONVERSATION_PASSWORD = "JGGdWPlhDqNO";
				CONVERSATION_URL = "db7521b1-f9f8-490a-8a38-c6b89606d8e4";*/
				
				CONVERSATION_USERNAME = APICredentials.CONVERSATION_USERNAME;
				CONVERSATION_PASSWORD = APICredentials.CONVERSATION_PASSWORD;
				CONVERSATION_URL = APICredentials.SCOPE_CONVERSATION_WORKSPACEID;
			}
		}
		
		
		
		s=s.toLowerCase();
		int start = s.indexOf("scopeandworkproduct"); //Taking all data between id "Payment term" and next header
		int end = 0;
		try{
		for (int i=start; ; i++){
			if(s.substring(i, i+16).equals("id=\"workproduct\"")){
				end = i;
				break;
			}
		}
		String text = s.substring(start, end);
		text = Jsoup.parse(text).text().replaceAll("scopeandworkproduct\">","");
		System.out.println(text);
		String[] sent=text.split(SplitPropSet.patternSplitSentence);
		ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
		service.setUsernameAndPassword(CONVERSATION_USERNAME, CONVERSATION_PASSWORD);

		String finalDisplay="";
		JSONObject jsondisplay = new JSONObject();
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		String how="";
		String what="";
		String why="";
		String who="";


		for(String sentence:sent){
			MessageRequest newMessage = new MessageRequest.Builder().alternateIntents(true).inputText(sentence).build();
			MessageResponse response = null;
			response = service.message(CONVERSATION_URL, newMessage).execute();
			String resp = response.toString();
			System.out.println(response);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(resp);
			System.out.println(json.get("intents"));

			for (int i=0; i < ((JSONArray)json.get("intents")).size(); i++){
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("What")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						flag1 = 1;
						what = what + sentence;
				}
					else{
						//jsondisplay.put("What", "Intent absent");
					}
				}
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("Why")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						flag2 = 1;
						why = why + sentence;
						}
					else{
						//jsondisplay.put("Why", "Intent absent");
					}
				}
				if (((JSONObject)((JSONArray)json.get("intents")).get(i)).get("intent").toString().equals("How")){
					if (Double.parseDouble(((JSONObject)((JSONArray)json.get("intents")).get(i)).get("confidence").toString()) >= 0.7){
						flag3 = 1;
						how = how + sentence;
					}else{
						//jsondisplay.put("How", "Intent absent");
					}
				}
				
			}

		}
		//==================================
		//changes made as per the new logic given by prashant where who is linked to general
		String tempdisplay="";
		int startWho = s.indexOf("id=\"general\"");
		int endWho = 0;
		
			for (int i = startWho;; i++) {
				if (s.substring(i, i + 3).equals("<h1")) {
					endWho = i;
					break;
				}
			}
			String textWho = s.substring(startWho, endWho);

			Pattern a = Pattern.compile(Pattern.quote("this statement of work")
					+ "(.*?)" + Pattern.quote("existing or future sows"));
			Matcher b = a.matcher(textWho);
			while (b.find()) {
				tempdisplay = Jsoup.parse(b.group(1).trim()).text();
				tempdisplay = "This Statement of Work " + tempdisplay
						+ " existing or future SOWs";
				System.out.println(tempdisplay);
			}

			Pattern q = Pattern.compile(Pattern.quote(by) + "(.*?)"
					+ Pattern.quote(under));
			Matcher n = q.matcher(textWho);
			while (n.find()) {
				who = n.group(1).trim();
			}
		
		//==================================
		jsondisplay.put("WHAT", what);
		jsondisplay.put("WHY", why);
		jsondisplay.put("WHO", who);
		jsondisplay.put("HOW", how);
		finalDisplay = jsondisplay.toString();
 
	        if((flag1==1)&&(flag2==1)&&(flag3==1)) //updated as per bug raised by prashant
	        {
	            System.out.println("Green");
	            System.out.println(finalDisplay);
	            dynamicPopUpContent.put("Scope", finalDisplay);
	            return true;
	        }
	        else{
	            System.out.println("Red");
	            System.out.println(finalDisplay);
	            dynamicPopUpContent.put("Scope", finalDisplay);
	            return false;
	                        
	        }
	        
	        }catch(Exception e){
	            e.printStackTrace();
	            System.out.println("catch exception");
	            dynamicPopUpContent.put("Scope", "Scope class missing!!!");
	            return false;
	        }    
	}

}




/**
 * 
 */
package com.ibm.contracts.advisor.parser;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.contracts.advisor.util.SOP;


/**
 * @author Atrijit
 *
 */
public class CiscoVcapUtils {
	
	//Here the variables for DashDB has not been used
	
	// object storage
	public static String accessKeyID = "";
	public static String secretAccessKey = "";
	public static String endpointUrl = "";
	
	// Variables for Document Conversion
	public static String VCAPDOCCONVERSION_url = "";
	public static String VCAPDOCCONVERSION_userID = "";
	public static String VCAPDOCCONVERSION_password = "";
	
	// conversation credentials
	public static String convUrl = "";
	public static String convUsername = "";
	public static String convPassword = "";
	
	
	// Environment variable for WorkSpace ID
	public static String ENV_CHAT_CLASS_WORKSPACE_ID = "";
	public static String ENV_CHAT_CONVERSATION_VERSION = "";
	public static String ENV_MP_WORKSPACE_ID = "";
	public static String ENV_MS_WORKSPACE_ID = "";
	public static String ENV_CONVERSATION_VERSION = "";
	public static String ENV_CONVERSATION_URL = "";

	
	/**
	 * 
	 */
	public CiscoVcapUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static JSONObject VCAPUtils() {
		String content = System.getenv("VCAP_SERVICES");
		System.out.println("content "+content);

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		//System.out.println("json "+json);
		try {
			json = (JSONObject) parser.parse(content);
			SOP.printSOPSmall("json is :: " + json);
			SOP.printSOPSmall("Type :: "+json.getClass().getName());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	
	public static void getConversationCredentials(JSONObject json) {
		JSONArray cred1 = (JSONArray) json.get("conversation-dedicated");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		convUrl = (String) obj1.get("url");
		convUsername = (String) obj1.get("username");
		convPassword = (String) obj1.get("password");

	}

	
	public static void getObjectStorageCredentials(JSONObject json) {
		JSONArray cred1 = (JSONArray) json.get("Object Storage Dedicated");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		accessKeyID = (String) obj1.get("accessKeyID");
		secretAccessKey = (String) obj1.get("secretAccessKey");
		endpointUrl = (String) obj1.get("endpoint-url");

	}
	
	
	public static void getDocumentConversionCredentials(JSONObject json) {
		// JSONArray cred1 = (JSONArray) json.get("document_conversion");
		JSONArray cred1 = (JSONArray) json.get("document_conversion-dedicated");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		VCAPDOCCONVERSION_url = (String) obj1.get("url");
		VCAPDOCCONVERSION_userID = (String) obj1.get("username");
		VCAPDOCCONVERSION_password = (String) obj1.get("password");

	}
	
	
	// The following function will get the environment variable(Not VCAP) for
		// the APP and set them with the variables of this class
		public static void setEnvironmentVariable() {
			ENV_CHAT_CLASS_WORKSPACE_ID = System.getenv("CHAT_CLASS_WORKSPACE_ID");
			ENV_CHAT_CONVERSATION_VERSION = System.getenv("CHAT_CONVERSATION_VERSION");
			ENV_MP_WORKSPACE_ID = System.getenv("MP_WORKSPACE_ID");
			ENV_MS_WORKSPACE_ID = System.getenv("MS_WORKSPACE_ID");
			ENV_CONVERSATION_VERSION = System.getenv("CONVERSATION_VERSION");
			ENV_CONVERSATION_URL = System.getenv("CONVERSATION_URL");			
					
			System.out.println("ENV_CHAT_CLASS_WORKSPACE_ID :: " + ENV_CHAT_CLASS_WORKSPACE_ID);
			System.out.println("ENV_CHAT_CONVERSATION_VERSION :: " + ENV_CHAT_CONVERSATION_VERSION);
			System.out.println("ENV_MP_WORKSPACE_ID :: " + ENV_MP_WORKSPACE_ID);
			System.out.println("ENV_MS_WORKSPACE_ID :: " + ENV_MS_WORKSPACE_ID);
			System.out.println("ENV_CONVERSATION_VERSION :: " + ENV_CONVERSATION_VERSION);
		}

	
	public static void main(String [] args){
		File jsonfile = new File("C:/IBMData/vcapcisco.json");
	}

	

}

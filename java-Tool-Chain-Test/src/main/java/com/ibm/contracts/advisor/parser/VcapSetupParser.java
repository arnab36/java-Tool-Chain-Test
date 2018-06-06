package com.ibm.contracts.advisor.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.util.SOP;

public class VcapSetupParser implements Constants {

	// Variables for DashDB
	public static String VCAPdbsslUrl = "";
	public static String VCAPdbUsername = "";
	public static String VCAPdbPassword = "";
	public static String VCAPdbjdbcurl = "";

	// variables for Object Storage
	public static String VCAPauth_url = "";
	public static String VCAPObjdomainName = "";
	public static String VCAPObjPassword = "";
	public static String VCAPObjUserId = "";
	public static String VCAPProject = "";

	// Variables for Document Conversion
	public static String VCAPDOCCONVERSION_url = "";
	public static String VCAPDOCCONVERSION_userID = "";
	public static String VCAPDOCCONVERSION_password = "";

	// Variables for Conversation
	public static String VCAPCONVERSATION_url = "";
	public static String VCAPCONVERSATION_userID = "";
	public static String VCAPCONVERSATION_password = "";

	// Environment variable for WorkSpace ID
	public static String ENV_CHAT_CLASS_WORKSPACE_ID = "";
	public static String ENV_CHAT_CONVERSATION_VERSION = "";
	public static String ENV_MP_WORKSPACE_ID = "";
	public static String ENV_MS_WORKSPACE_ID = "";
	public static String ENV_CONVERSATION_VERSION = "";
	public static String ENV_SCOPE_CONVERSATION_WORKSPACEID = "";

	public VcapSetupParser() {
		// TODO Auto-generated constructor stub
	}

	public static JSONObject VCAPUtils() {
		String content = System.getenv("VCAP_SERVICES");
		System.out.println("content " + content);

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		// System.out.println("json "+json);
		try {
			json = (JSONObject) parser.parse(content);
			SOP.printSOPSmall("json is :: " + json);
			SOP.printSOPSmall("Type :: " + json.getClass().getName());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	// Read DashDB credential from VCAP
	public static void getDashDBCredential(JSONObject json) {

		JSONArray cred1 = (JSONArray) json.get("dashDB");
		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		VCAPdbsslUrl = (String) obj1.get("ssljdbcurl");
		VCAPdbUsername = (String) obj1.get("username");
		VCAPdbPassword = (String) obj1.get("password");
		VCAPdbjdbcurl = (String) obj1.get("jdbcurl");
	}

	// Read Object Storage credentials from VCAP
	public static void getObjectStorageCredentials(JSONObject json) {

		JSONArray cred1 = (JSONArray) json.get("Object-Storage");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		VCAPauth_url = (String) obj1.get("auth_url");
		if (!VCAPauth_url.endsWith(OBJECTSTORAGEVERSION)) {
			VCAPauth_url = VCAPauth_url + OBJECTSTORAGEVERSION;
		}
		VCAPObjdomainName = (String) obj1.get("domainName");
		VCAPObjPassword = (String) obj1.get("password");
		VCAPObjUserId = (String) obj1.get("userId");
		VCAPProject = (String) obj1.get("project");

		SOP.printSOPSmall("========= Inside===============getObjectStorageCredentials ========================");
		SOP.printSOPSmall("VCAPDomain Name :: " + VCAPObjdomainName);
		SOP.printSOPSmall("VCAPProject :: " + VCAPProject);
		SOP.printSOPSmall("VCAPauth_url :: " + VCAPauth_url);
		SOP.printSOPSmall("VCAPObjUserId :: " + VCAPObjUserId);
		SOP.printSOPSmall("VCAPObjPassword :: " + VCAPObjPassword);
		SOP.printSOPSmall("========= Getting Out of ===============getObjectStorageCredentials ========================");

		System.out.println("============== VCAP Project is :: " + VCAPProject);
	}

	// The following function will return the document conversion credential
	// from VCAP
	public static void getDocumentConversionCredentials(JSONObject json) {
		JSONArray cred1 = (JSONArray) json.get("document_conversion");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		VCAPDOCCONVERSION_url = (String) obj1.get("url");
		VCAPDOCCONVERSION_userID = (String) obj1.get("username");
		VCAPDOCCONVERSION_password = (String) obj1.get("password");

	}

	// The following function will return the conversation credential from VCAP
	public static void setConversationCredentials(JSONObject json) {
		JSONArray cred1 = (JSONArray) json.get("conversation");

		JSONObject obj = new JSONObject();
		obj = (JSONObject) cred1.get(0);

		JSONObject obj1 = new JSONObject();
		obj1 = (JSONObject) obj.get("credentials");

		VCAPCONVERSATION_url = (String) obj1.get("url");
		VCAPCONVERSATION_userID = (String) obj1.get("username");
		VCAPCONVERSATION_password = (String) obj1.get("password");
	}

	// The following function will get the environment variable(Not VCAP) for
	// the APP and set them with the variables of this class
	public static void setEnvironmentVariable() {
		ENV_CHAT_CLASS_WORKSPACE_ID = System.getenv("CHAT_CLASS_WORKSPACE_ID");
		ENV_CHAT_CONVERSATION_VERSION = System.getenv("CHAT_CONVERSATION_VERSION");
		ENV_MP_WORKSPACE_ID = System.getenv("MP_WORKSPACE_ID");
		ENV_MS_WORKSPACE_ID = System.getenv("MS_WORKSPACE_ID");
		ENV_CONVERSATION_VERSION = System.getenv("CONVERSATION_VERSION");
		ENV_SCOPE_CONVERSATION_WORKSPACEID = System.getenv("SCOPE_CONVERSATION_WORKSPACEID");		
				
		System.out.println("ENV_CHAT_CLASS_WORKSPACE_ID :: " + ENV_CHAT_CLASS_WORKSPACE_ID);
		System.out.println("ENV_CHAT_CONVERSATION_VERSION :: " + ENV_CHAT_CONVERSATION_VERSION);
		System.out.println("ENV_MP_WORKSPACE_ID :: " + ENV_MP_WORKSPACE_ID);
		System.out.println("ENV_MS_WORKSPACE_ID :: " + ENV_MS_WORKSPACE_ID);
		System.out.println("ENV_CONVERSATION_VERSION :: " + ENV_CONVERSATION_VERSION);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// getDashDBCredential(VCAPUtilsTest());
		// getObjectStorageCredentials(VCAPUtilsTest());
		String filePath = "C:/Users/IBM_ADMIN/Documents/Phase2/envVariables.json";
		try {
			// File file = new File(filePath);

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(filePath));

			JSONObject jsonObject = (JSONObject) obj;
			getDocumentConversionCredentials(jsonObject);

			System.out.println(VCAPDOCCONVERSION_url);
			System.out.println(VCAPDOCCONVERSION_userID);
			System.out.println(VCAPDOCCONVERSION_password);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

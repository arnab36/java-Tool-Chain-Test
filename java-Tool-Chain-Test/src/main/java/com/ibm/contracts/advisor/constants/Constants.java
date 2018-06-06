package com.ibm.contracts.advisor.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public interface Constants {
		
		//public static final String clientName = "CISCO";
		
		public static final String auth_url = "https://identity.open.softlayer.com/v3"; //credentials.get("auth_url").toString() + "/v3";	
		public static final Charset ENCODING = StandardCharsets.UTF_8;
				
		public static final String FALSEPOSITIVE = "Y";
		public static final String FALSEPOSITIVEYES = "Y";		
		public static final String RESOURCE_FILE_NAME = "UtilConfigurationParameters.properties";
		
		public static final String PROPERTIES_CONTAINER = "PropertiesContainer-V2";
		public static final String PROCESSING_CONTAINER="ProcessingContainer-V2"; // "bluemix";
		public static final String STRATEGY_CONTAINER="StrategyContainer-V2";		
		public static final String SAVE_DIR = "uploadFiles";
		
		public static final String UPLOADCONFIGURATION_CONTAINER = "ProcessingContainer"; 
		
		public static final String FURTHERSPLIT = "Y";
		public static final String FURTHERSPLITYES = "Y";
		
		public static  final String credentialTableName = "USERCREDENTIALSV2";
		public static  final String strategyTableName = "USERSTRATEGYV2";
		public static  final String documentTableName = "DOCUMENTSV2";
		
		public static final String configFileName = "docConfigHtml.json";
		//public static final String templateFileName = "Template_MP_Classes.json";
						
		//Document Conversion
		public static final String DOC_CONV_1 = "87d55b69-a32e-47ff-be30-20959855ca2a";
		public static final String DOC_CONV_2 = "C3ppYdLMxPoA";
		
		public static final String CISCO_DOC_CONV_1 = "451ca8d3-f8a5-4745-bb06-76b5a1ee6f33";
		public static final String CISCO_DOC_CONV_2 = "HOecaT5dIuNE";
			  		
		//Encryption key
		public static final String ENCRYPTIONKEY = "dsaihfsdhfis423efw532432";
		
		//SSL TO DO. To be moved into servlet
		public static final String FORWARDPATH = "/index.html";
		
		//VCAP
		public static final boolean VCAPDASHDB = true;
		public static final boolean VCAPOBJECTSTORAGE = true;	
		
		public static final boolean VCAPDOCUMENTCONVERSION = true;
		public static final boolean VCAPCONVERSATION = true;
		
		// Clear Object storage after Analyze
		public static final boolean CLEAROBJECTSTORAGE = false;	
		
		public static final double fuzzeThreshold = 0.0;		
		public static final int APIFailPer=10; 		
		public static final String OBJECTSTORAGEVERSION = "/v3";
		
		public static final boolean APACHETIKADOCUMENTCONVERSION = true;
		
		
		/* Object storage  Credentials for CISCO */			
		public static final String userId = "12261e45321b4702a57965bc59fcc6d3";//credentials.get("userId").toString();
		public static final String password = "Fpn{kLF2N87X.,yN";//credentials.get("password").toString();
		public static final String domain = "1278799"; //credentials.get("domainName").toString();
		public static final String project = "object_storage_310a9aa2_178a_4c9d_9919_3de1d4da3922" ;
		
		
		/* 
		 * Object storage  Credentials for ARNAB for Error handling purpose.
		 * The following object storage has a blank strategy Container
		 * 
		 */					
	/*	public static final String userId = "1ed3aa409f9c4ba1ac64b4157f412eb2";//credentials.get("userId").toString();
		public static final String password = "KzDIPOf#4_xI0s-L";//credentials.get("password").toString();
		public static final String domain = "1123407"; //credentials.get("domainName").toString();
		public static final String project = "object_storage_78b1f253_a7b6_4e21_aa9f_d6e3a35d4616" ;*/
		
		
		// For VCAP (for Object storage) the above has been commented and the following variables are cleared		
		/*public static final String userId = "";
		public static final String password = "";
		public static final String domain = "";
		public static final String project = "" ;*/
		
		// Version 2 table credential
		/*public static final String dbClassName = "com.ibm.db2.jcc.DB2Driver";
		public static final String dbUrl = "jdbc:db2://bluemix05.bluforcloud.com:50000/BLUDB";
		public static final String dbUsername = "dash113539";
		public static final String dbPassword = "FNa1E##s1Xfl";	*/
		
		
		/*
		 *  This is the credential for Arnab's database. This is an empty one.
		 *  It is used for error handling.
		 */
		/*public static final String dbClassName = "com.ibm.db2.jcc.DB2Driver";
		public static final String dbUrl = "jdbc:db2://dashdb-entry-yp-dal09-10.services.dal.bluemix.net:50000/BLUDB";
		public static final String dbUsername = "dash7663";
		public static final String dbPassword = "Lg$CW8whr$E1";	*/
		
		
		// For VCAP (for DAshDB )the above has been commented and the following variables are cleared		
		public static final String dbClassName = "com.ibm.db2.jcc.DB2Driver";
		public static final String dbUrl = "";
		public static final String dbUsername = "";
		public static final String dbPassword = "";			
		
		public static String dashDBURL=dbUrl+":user="+dbUsername+";password="+dbPassword+";";
		public static final String poolName = "local";
		public static final int minPool = 5;
		public static final int maxPool = 10;
		public static final int maxSize = 30;
		public static final long idleTimeout = 180;
		
		public static final String localContainer = "C:/Users/IBM_ADMIN/Documents/Phase2/localContainer/";				
		
		public static String removeRegex = "((?=\\S)[\\*]+(?<=\\S))|((?=\\S)[\\(]?[\\d]+[\\)]?(?<=\\S))|((?=\\S)[\\(]?[A-Za-z][\\)](?<=\\S))";
		public static String[] headerTagList = { "h1", "h2", "h3", "h4", "h5","h6" };				
		public static double staticScoreThreshold = 80;	
		public static double staticGeneralThreshold = 80;
		public static double fuzzySentencematchingForStatic = 80;
		
		/**
		 *  
		 * The following is the work-flow-config part.
		 */		
		
		public final static String classification="CONV";//or conv
		public final static boolean withFormula=false; //if classification is conv then it will be true
		public final static int lessWC =10; //for cisco 
		/*changes class name and confidence for two special class will fall under this category which
		is applicable for CISCO*/
		public final static String splitType="sentence";
		
		public final static int workFlowConfigWORDCOUNT=10;
		
	//======================== dont change below=====================================
		
		public static  String documentSplit = "Y";
		public static boolean chunked=false;
		public static final String YES = "Y";
		public static final String NO = "N";		
		
		public static final int THREADSNLC = 30;
		public static final int THREADSCONVERSATION = 30;		
		
		public static final int NLCRETRIES = 3;
		public static final int CONVERSATIONRETRIES = 3;
		public static final int CHATRETRIES = 3;
		
				
		/**
		 * The following is the APICredential part
		 */
		
		/*public static  String NLC_LANG_Classifier_ID = "359f41x201-nlc-85931";
		public static  String nlcusername = "0b867a3a-9e18-4817-904b-5e3bce6f7707";
		public static  String nlcpassword = "4JzFK5vxuD8D";		
			
		public static final String workspace_id_NLC_DEL_V1_Cisco = "17de4158-b850-47c7-b69f-77effc0843f7";
				
		public static  String CHAT_CONV_USERID = "eb88d8fc-94d6-4f0d-a2c3-83ed3347102b";
		public static  String CHAT_CONV_PASSWORD = "JGGdWPlhDqNO";
		public static  String CHAT_CLASS_WORKSPACE_ID = "2e90c529-035b-4663-96a6-71da2f50253e";
		public static  String CHAT_CONVERSATION_VERSION = "2017-05-26";
		
		//public static  String CLASS_WORKSPACE_ID = "2e90c529-035b-4663-96a6-71da2f50253e";
		public static String CLASS_WORKSPACE_ID = null;
		public static String MP_WORKSPACE_ID="9198c73d-9952-4382-aed2-f727fb53278e";
		public static String MS_WORKSPACE_ID="8ec782d8-5379-45e5-973c-c972605a2af6";

		public static  String CONVERSATION_USERNAME = "eb88d8fc-94d6-4f0d-a2c3-83ed3347102b";
		public static  String CONVERSATION_PASSWORD = "JGGdWPlhDqNO";
		public static  String CONVERSATION_VERSION = "2017-04-21";
		*/
		
		/**
		 * 
		 *  The following is splitPropSet 
		 * 
		 * */
		
		public static String patternWords="([a-zA-Z_]+)";
		
		public static String patternSplitSentence="(([.;])(\\s+)|((\r|\n){2,}))(?=[A-Z])";
		public static String patternTabSplit="(\\s+[R]\\s+)";
		
		public static int DWORDCOUNT=140;
		public static int SplitPropSetWORDCOUNT=70;
		public static String patternSplitSentDSpace="\\b\\s{2,}\\b";
		
		public static String patternCISCO="cisco|CISCO|Cisco";
		public static String saveAfterSplitChunk="C:/Users/IBM_ADMIN/Documents/project 6/json samples/javaAfterSplit_chunk.json";
		public static String pathMyDoc="../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/MyDoc.json";

		public static String templateString=null;
		public static JSONObject customConfig=null;
		public static String tmpJSON=null;
		public static HashMap<String,String> templateMap = new HashMap<String,String>(); 
		
		public static String patternEnum = "\\s(\\.{1,3})(\\.)";
		public static String patternSplitSentenceNew="(?<=[a-z])\\.\\s+";
		
		// For Dynamic processing 
		public static final String  SpecialTerms = "SpecialTerms";
		public static final String  Alert1 = "Additional Special Terms found in the contract. Review required.";
		public static final String  alert1regex ="(?<=\\<p)(.+?)(?=\\<\\/p\\>)";

		public static final String  OtherSupplierResources = "OtherSupplierResources";
		public static final String  Alert2 = " Other Supplier Resources table populated. Buyer review required.";
		public static final String  alert2regex ="(?<=\\<td\\>)(.+?)(?=\\<\\/td\\>)";

		public static final String  PaymentMethod = "PaymentMethod";
		public static final String  Alert3 = "Pre-payment term found in the Payment Method Table. Review required.";
		public static final String  alert3regex = "(?<=\\<tbody\\>)(.+?)(?=\\<\\/tbody\\>)";
		
		public static final String by = "by";
		public static final String under = "under";
		
		// for cisco VCAP
		public static final boolean CISCOVCAP = false;
		public static final String DBSCHEMANAME = "IBM_TEST_PROJ_TEST1_DEV";
		
		// For document type selection
		public static final String MPNonFG = "MP - Non FG";
		public static final String MSNonFG = "MS - Non FG";
		public static final String MPFG = "MP - FG";
		public static final String MSFG = "MS - FG";
		
		// For Threading purpose
		
		// two separate Document Conversion
		public static final boolean uploadServletFirstThread = true;
		public static final int uploadServletFirstThreadCount = 2;
		public static final String UploadServletAnswers = "answers";
		public static final String UploadServletConvertRes = "convertRes";
		public static final int UploadServletFirstThreadSleepTime = 100;
		
		// For setting File from Object Storage
		public static final boolean uploadServletSecondThread = true;
		public static final int uploadServletSecondThreadCount = 2;
		public static final String UploadServletSecondThreadTemplateFileName = "templateFileName";
		public static final String UploadServletSecondThreadStaticTemplateName = "staticTemplateName";
		public static final int UploadServletSecondThreadSleepTime = 100;
		
		// For Static and Dynamic parallel processing 
		public static final boolean analyseServletFirstThread = false;
		public static final int analyseServletFirstThreadCount = 2;
		public static final String analyseServletFirstThreadProcessGeneral = "GENERAL";
		public static final String analyseServletFirstThreadProcessynamic = "DYNAMIC";
		public static final int analyseServletFirstThreadThreadSleepTime = 4000;
		
		// For calling 6 object storage calls parallely 
		public static final boolean analyseServletSecondThread = true;
		public static final int analyseServletSecondThreadCount = 6;
		public static final String analyseServletGeneralHierarchy = "GENERALHIERARCHYCALL";
		public static final String analyseServletStaticHierarchy = "STATICHIERARCHYCALL";
		public static final String analyseServletDynamicHierarchy = "DYNAMICHIERARCHYCALL";
		public static final String analyseServletGeneralHeplClass = "GENERALHELPCLASSCALL";
		public static final String analyseServletStaticHelpClass = "STATICHELPCLASSCALL";
		public static final String analyseServletDynamicHelpClass = "DYNAMICHELPCLASSCALL";
		public static final int analyseServletSecondThreadThreadSleepTime = 100;
		
		
}




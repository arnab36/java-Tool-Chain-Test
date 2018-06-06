package com.ibm.contracts.advisor.constants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PostNLCPropSet {
public static String	titleQuery = "Statement of Work";

public static String patternSup = "Cisco|IBM";
public static String patternComp ="AAA|ExxonMobil|Pensiamo|TMNA|PPG";
//public static String titleClass= "{'CLASS': 'Title', 'confidence': 0.0, 'Quality': 1, 'class_alternatives':'' }";
//public static String Deliverables = "{"0":0, "1":1, "2":2, '3':3}"; 
private static Map<String, String> titleClass=new HashMap<String,String>();
public static JSONObject stdLanguage=null;

public static double classProb=0.7;

public static List<String> templateList;
public static  String stdLanguagefile = "../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/StdLanguage.json";
//public static final String afterSplitJSON="../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/OPForSplit_after.json";
//public static  String templatefile = "../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/Template_IBM_JSON.json";
public static  String templatefilePath = "../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/Template_IBM_JSON.json";
public static  JSONObject templatefile=null;
public static  JSONObject staticTemplatefile=null;

public static  String Failed = "";
// It is for runninr driver programs
//public static final String templatefile = "C:/Users/IBM_ADMIN/Documents/Phase2/ContractsAdvisor/src/main/webapp/WEB-INF/Template_IBM_JSON.json";

public static Map getTitleClass(){
	titleClass.put("CLASS", "Title");
	titleClass.put("confidence", "0.0");
	titleClass.put("Quality", "1");
	titleClass.put("class_alternative", "");
	return titleClass;
	
}
public static  LinkedList<String> moneyClass=new LinkedList<String>();
//money_classes = ["TMTerms","FPTerms","TravelTerms","Header","PaymentTerms","BLANK"]
public static void intiMoneyClass(){
	moneyClass.add("TMTerms");
	moneyClass.add("FPTerms");
	moneyClass.add("TravelTerms");
	moneyClass.add("Header");
	moneyClass.add("PaymentTerms");
	moneyClass.add("BLANK");
	
	/*moneyClass.add("TMTerms");
	moneyClass.add("FPTerms");
	moneyClass.add("TravelTerms");
	moneyClass.add("Header");
	moneyClass.add("PaymentTerms");
	moneyClass.add("BLANK");*/
}


/*
*     euro_numbers =  re.findall(r'(\d{1,4}(\.?\d{3})*(\,\d{2}))|(\d{3}(\,\d{3})*(\.\d{2})?)',content_string)
us_numbers =  re.findall(r'(\d{1,4}(\,\d{3})*(\.\d{2})?)|(\d{3}(\,\d{3})*(\.\d{2})?)',content_string)
canada_numbers =  re.findall(r'(\d{1,4}(\,\d{3})*(\.\d{2})?)|(\d{3}(\,\d{3})*(\.\d{2})?)',content_string)*/

// Previous
/*public static final Pattern patternUs = Pattern.compile("(\\d{1,3}(\\,\\d{3})*(\\.\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternCanada = Pattern.compile("(\\d{1,4}(\\,\\d{3})*(\\.\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\.\\d{3})*(\\,\\d{2}))");*/

public static final Pattern patternUs = Pattern.compile("(\\d{1,3}(\\,\\d{2,3})*(\\.\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternCanada = Pattern.compile("(\\d{1,4}(\\,\\d{3})*(\\.\\d{2})?)|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\.\\d{3})*(\\,\\d{2}))");


/* previous pattern
 * public static final Pattern patternUs = Pattern.compile("(\\d{1,4}(\\,\\d{3})*(\\.\\d{2})?)|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternCanada = Pattern.compile("(\\d{1,4}(\\,\\d{3})*(\\.\\d{2})?)|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)"); */

/*public static final Pattern regTM = Pattern.compile("\b((Time)\\s(and)\\s(Material(s)?)\\s(basis))|((Time)\\s(&)\\s(Material(s)?)\\s(basis))\b",Pattern.CASE_INSENSITIVE); 

public static final Pattern regFP =Pattern.compile("\b((fixed)\\s(price)\\s(contract))|((fixed)\\s(price)\\s(basis))|((fixed)\\s(price))\b");
*/


public static final Pattern regTM = Pattern.compile("((Time)\\s(and)\\s(Material(s)?)\\s(basis))|((Time)\\s(&)\\s(Material(s)?)\\s(basis))",Pattern.CASE_INSENSITIVE); 
					//"\b((Time)\s(and)\s(Material(s)?)\s(basis))|((Time)\s(&)\s(Material(s)?)\s(basis))\b"
public static final Pattern regFP =Pattern.compile("((fixed)\\s(price)\\s(contract))|((fixed)\\s(price)\\s(basis))|((fixed)\\s(price))",Pattern.CASE_INSENSITIVE);

public static String patternSplitSentence="(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|(?<=[^(i.e)])([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\()[a-zA-Z0-9][\\).](\\s+)(?=[A-Z]))|((\\s+)[a-zA-Z0-9][\\).](\\s+)(?=[A-Z])))";
/*
public static final Pattern regTM = Pattern.compile("\b((Time)\\s(and)\\s(Material(s)?)\\s(basis))|((Time)\\s(&)\\s(Material(s)?)\\s(basis))\b",Pattern.CASE_INSENSITIVE); 
public static final Pattern regFP =Pattern.compile("\b((fixed)\\s(price)\\s(contract))|((fixed)\\s(price)\\s(basis))|((fixed)\\s(price))\b");


public static final Pattern patternUs = Pattern.compile("(\\d{1,3}(\\,\\d{3})*(\\.\\d{2}))|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternCanada = Pattern.compile("(\\d{1,4}(\\,\\d{3})*(\\.\\d{2})?)|(\\d{3}(\\,\\d{3})*(\\.\\d{2})?)");
public static final Pattern patternEuro = Pattern.compile("(\\d{1,4}(\\.?\\d{3})*(\\,\\d{2}))|(\\d{3}(\\.\\d{3})*(\\,\\d{2}))");  */

}

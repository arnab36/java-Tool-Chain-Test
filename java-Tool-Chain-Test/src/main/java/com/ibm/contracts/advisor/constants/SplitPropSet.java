package com.ibm.contracts.advisor.constants;

import java.util.HashMap;

import org.json.simple.JSONObject;



public class SplitPropSet {
public static String patternWords="([a-zA-Z_]+)";

public static String patternTabSplit="(\\s+[R]\\s+)";
//'([.;])(?=\s*[A-Za-z])\s*'
public static int DWORDCOUNT=140;
public static int WORDCOUNT=70;
public static String patternSplitSentDSpace="\\b\\s{2,}\\b";
//'\b\s{2,}\b'
public static String patternEnum = "\\s(.{1,3})(\\.)";
//pattern_enum = re.compile(r'\s(.{1,3})(\.)')
public static String patternCISCO="cisco";
//pattern_Cisco = re.compile(r'Cisco', re.IGNORECASE)
public static String saveAfterSplitChunk="C:/Users/IBM_ADMIN/Documents/project 6/json samples/javaAfterSplit_chunk.json";
//public static String saveAfterSplitSent="C:/Users/IBM_ADMIN/Documents/project 6/json samples/javaAfterSplit_sent.json";
public static String pathMyDoc="../defaultServer/apps/expanded/ContractsAdvisor.war/WEB-INF/MyDoc.json";



//public static String patternSplitSentencev5="(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\(?)[a-zA-Z0-9][\\).](\\s+)(?=[A-Z])))";//

//public static String patternSplitSentence="(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|(?<=[^(i.e)])([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\()[a-zA-Z0-9][\\).](\\s+)(?=[A-Z]))|((\\s+)[a-zA-Z0-9][\\).](\\s+)(?=[A-Z])))";

public static String patternSplitSentence="(((\\s+\\(?)([Ii][Xx]|[Ii][Vv]|[Vv]?[Ii]{1,3})[.\\)](\\s+))|([.;:])(\\s+)|((\r|\n|\\s){2,})|((\\s+\\()[a-zA-Z0-9][\\).](\\s+)(?=[A-Z]))|((\\s+)[a-zA-Z0-9][\\).](\\s+)(?=[A-Z])))";

}





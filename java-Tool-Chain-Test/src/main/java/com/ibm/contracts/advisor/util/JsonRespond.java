package com.ibm.contracts.advisor.util;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class JsonRespond {

	public JsonRespond() {
		// TODO Auto-generated constructor stub
	}
	
	public static void respond(String responseString, HttpServletResponse response, int status) throws Exception{
		response.setStatus(status);
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/x-json");
		response.setContentLength(responseString.getBytes("UTF8").length);
		PrintWriter writer =  new PrintWriter(
		new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
		writer.write(responseString);
		writer.close();
	}
	
	public static void createResponseJSON(String data,String error,HttpServletResponse response, int status) throws Exception{
		JSONObject res=new JSONObject();
		res.put("data", data);
		res.put("error", error);
		//res.put("jsonResponse", json);		
		JsonRespond.respond(res.toJSONString(),  response,  status);		
	}
	
	public static void createResponseForHomeToViewStrategyJSON(String generalHierarchy,HttpServletResponse response, int status) throws Exception{
		JSONObject res=new JSONObject();		
		res.put("generalHierarchy", generalHierarchy);
		JsonRespond.respond(res.toJSONString(),  response,  status);		
	}
	
	
	public static void createResponseJSON(String json,String data,String error,HttpServletResponse response, int status) throws Exception{
		JSONObject res=new JSONObject();
		res.put("data", data);
		res.put("error", error);
		res.put("jsonResponse", json);
		System.out.println("Result in create response :: "+ json);
		JsonRespond.respond(res.toJSONString(),  response,  status);		
		
	}
	
	public static void uploadServletResopnse(String data, String error,
			HttpServletResponse response, String fileName, int status) throws Exception{
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("data", data);
		res.put("error", error);
		res.put("fileName", fileName);
		
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	// For CISCO temporary prupose
	public static void createResponseJSON(HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createResponseJSON(String jsonString, String log,
			double score, String error, HttpServletResponse response, int status) throws Exception{
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("staticResult", jsonString);
		res.put("staticScore", score);
		res.put("error", error);
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createResponseJSON(String staticResult,
			String generalResult, String log, double score, String error, String generalHierarchy, String staticHierarchy, String generalHelpClasses,
			String staticHelpClasses,String dynamicAlert, String dynamicResult,String dynamicHierarchy,String dynamicHelpClases, 
			String dynamicPopUpContent, String missingStatic, HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("staticResult", staticResult);
		res.put("staticHierarchy", staticHierarchy);
		res.put("staticScore", score);
		res.put("missingStatic", missingStatic);
		res.put("error", error);
		res.put("generalResult", generalResult);
		res.put("generalHierarchy", generalHierarchy);
		res.put("staticHelpClasses", staticHelpClasses);
		res.put("generalHelpClasses", generalHelpClasses);
		res.put("dynamicAlert", dynamicAlert);
		res.put("dynamicResult", dynamicResult);
		res.put("dynamicHierarchy", dynamicHierarchy);
		res.put("dynamicHelpClases", dynamicHelpClases);
		res.put("dynamicPopUpContent", dynamicPopUpContent);
		
		res.put("log", log);
		
		JsonRespond.respond(res.toJSONString(), response, status);
		
	}

	public static void createLoginResponseJSON(String jsonObject,
			String strategyInfoHierarchy, HttpServletResponse response, int status ) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("jsonObject",jsonObject);
		res.put("strategyInfoHierarchy",strategyInfoHierarchy);
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createLoginResponseJSON(String jsonObject,
			HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("jsonObject",jsonObject);
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createStrategyHierarchyResponseJSON(String classHierarchyCISCO_GEN_MP,
			String classHierarchyCISCO_GEN_MS,	HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("classHierarchyCISCO_GEN_MP", classHierarchyCISCO_GEN_MP);
		res.put("classHierarchyCISCO_GEN_MS", classHierarchyCISCO_GEN_MS);
		//res.put("classHierarchyCISCO_STATIC_MP", classHierarchyCISCO_STATIC_MP);
		//res.put("classHierarchyCISCO_STATIC_MS", classHierarchyCISCO_STATIC_MS);		
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createStrategyHierarchyResponseJSON(String strategyInfo,
			HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("strategyInfo", strategyInfo);
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createStrategyHierarchyResponseJSONAdmin(String strategyHierarchy,
			String strategyInfo,HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("strategyHierarchyInfo", strategyHierarchy);
		res.put("strategyInfo", strategyInfo);
		JsonRespond.respond(res.toJSONString(), response, status);
	}

	public static void createSingleStrategyResponseJSON(String singleStrategyInfo,
			HttpServletResponse response, int status) throws Exception {
		// TODO Auto-generated method stub
		JSONObject res=new JSONObject();
		res.put("singleStrategyInfo", singleStrategyInfo);	
		JsonRespond.respond(res.toJSONString(), response, status);		
	}

}

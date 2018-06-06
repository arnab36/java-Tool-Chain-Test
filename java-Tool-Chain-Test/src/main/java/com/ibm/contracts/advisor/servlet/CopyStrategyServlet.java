package com.ibm.contracts.advisor.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.implementation.StrategyDAO;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.Util;



public class CopyStrategyServlet extends HttpServlet implements Constants{ 
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException { 
		
		ServletContext sc = this.getServletContext();
		HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		
		String strategyUserid = null;
		String strategyType = null;		
		String strategyName =null;
		String newStrategyName =null;
		String date = null;
		String accessType = null;
		
		String userid = null;
		try {
			//userid = request.getParameter("userId");
			userid = (String)session.getAttribute("userId");
		} catch (Exception e1) {			
			e1.printStackTrace();
		}
		
		try {			
			strategyUserid=request.getParameter("userId");
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {			
			strategyType=request.getParameter("stratType");
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {			
			newStrategyName=request.getParameter("new_strategy_name");
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {			
			strategyName=request.getParameter("strategyName");
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {			
			date=request.getParameter("date");
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		System.out.println("userid :: "+userid);
		System.out.println("strategyUserid :: "+strategyUserid);
		System.out.println("strategyType :: "+strategyType);
		System.out.println("strategyName :: "+strategyName);
		System.out.println("newStrategyName :: "+newStrategyName);
		
		JSONObject strategyObject = extractStrategyInformation(userid);
		JSONObject strategyToCopyInfo = new JSONObject();
		
		if(strategyObject != null){
			List<Object> arrList = new ArrayList<Object>();
			String tempName = "";
			arrList = (List<Object>) strategyObject.get(strategyType);
			for(int i = 0; i < arrList.size(); i++){ 
				tempName = (String)(((HashMap) arrList.get(i)).get("name"));
				System.out.println(tempName);
				if(tempName.equalsIgnoreCase(strategyName)){
					strategyToCopyInfo = (JSONObject)(arrList.get(i));					
					break;
				}
			}
		}
		
		
		strategyToCopyInfo.put("name", newStrategyName);
		strategyToCopyInfo.put("Date", date);
		accessType  = strategyToCopyInfo.get("access").toString().substring(0, 3);
		
		System.out.println("Strategy info we got is :: " + strategyToCopyInfo);	
		
		JSONObject strategyDestinationObject = extractStrategyInformation(strategyUserid);
		System.out.println("Strategy destination object :: \n " + strategyDestinationObject);
		
		if(strategyDestinationObject != null){
			List<Object> arrList1 = new ArrayList<Object>();
			arrList1 = (List<Object>) strategyDestinationObject.get(strategyType);
			arrList1.add(strategyToCopyInfo);
		}
		
		System.out.println("================================================================");
		System.out.println("Strategy destination object :: \n " + strategyDestinationObject);
		
		storeInObjectStoage(strategyDestinationObject,userid);
		
		StrategyDAO.insertIntoStrategyTable(userid, newStrategyName, accessType, strategyType);
		
		try {
			JsonRespond.createResponseJSON(strategyDestinationObject.toJSONString(),accessType, response, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void storeInObjectStoage(JSONObject strategyDestinationObject, String userid) {
		
		String jsonData = strategyDestinationObject.toJSONString();
		try {
			ObjectStoreHandler.delete(STRATEGY_CONTAINER, userid
					+ "_strategy.json");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// Adding to Object Storage
		try {
			ObjectStoreHandler.objectStore(
					new ByteArrayInputStream(jsonData.getBytes()), userid
							+ "_strategy.json", STRATEGY_CONTAINER);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	private static JSONObject extractStrategyInformation(String userid){
		String sytaregyFile = "";
		JSONObject strategyObject = null;		
		JSONParser parser = new JSONParser();
		
			try {
				strategyObject =Util.getJSONObject(ObjectStoreHandler.getFileStr(userid+"_strategy.json", STRATEGY_CONTAINER)) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		return strategyObject;		 
	}
	
	
	public static void main(String[] ab){
		
		String strategyUserid = "demo_user1@cisco.com";
		String strategyType = "MP";		
		String strategyName = "test";
		String newStrategyName ="copied";
		String date = "02/07/2017";
		String accessType = "Pri";
		
		String userid = "demo_user1@cisco.com";
		
		JSONObject strategyObject = extractStrategyInformation(userid);
		JSONObject strategyToCopyInfo = new JSONObject();
		
		if(strategyObject != null){
			List<Object> arrList = new ArrayList<Object>();
			String tempName = "";
			arrList = (List<Object>) strategyObject.get(strategyType);
			for(int i = 0; i < arrList.size(); i++){ 
				tempName = (String)(((HashMap) arrList.get(i)).get("name"));
				System.out.println(tempName);
				if(tempName.equalsIgnoreCase(strategyName)){
					strategyToCopyInfo = (JSONObject)(arrList.get(i));					
					break;
				}
			}
		}
		
		
		strategyToCopyInfo.put("name", newStrategyName);
		strategyToCopyInfo.put("Date", date);
		accessType  = strategyToCopyInfo.get("access").toString().substring(0, 3);
		
		System.out.println("Strategy info we got is :: " + strategyToCopyInfo);	
		
		JSONObject strategyDestinationObject = extractStrategyInformation(strategyUserid);
		System.out.println("Strategy destination object :: \n " + strategyDestinationObject);
		
		if(strategyDestinationObject != null){
			List<Object> arrList1 = new ArrayList<Object>();
			arrList1 = (List<Object>) strategyDestinationObject.get(strategyType);
			arrList1.add(strategyToCopyInfo);
		}
		
		System.out.println("================================================================");
		System.out.println("Strategy destination object :: \n " + strategyDestinationObject);
		
		/*storeInObjectStoage(strategyDestinationObject,userid);		
		StrategyDAO.insertIntoStrategyTable(userid, newStrategyName, accessType, strategyType);*/
		
		
	}
	
	
	
	
	
}


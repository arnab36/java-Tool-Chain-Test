package com.ibm.contracts.advisor.servlet;

import java.io.FileReader;
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

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.Util;

/**
 * This servlet will provide a single strategy info based on user selection
 * */

public class GetStrategyInfoFromObjectStorage extends HttpServlet implements
		Constants {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletContext sc = this.getServletContext();
		HttpSession session = SessionHelper.sessionHelp(request, sc, response);

		String userid = null;
		String strategyType = null;
		String allInfo = null;
		String strategyName = null;

		JSONObject strategyClassInfo = new JSONObject();

		try {
			userid = request.getParameter("userId");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			strategyType = request.getParameter("stratType");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			allInfo = request.getParameter("allInfo");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			strategyName = request.getParameter("strategyName");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		System.out.println("UserID :: " + userid);
		System.out.println("strategyType :: " + strategyType);
		System.out.println("strategyName :: " + strategyName);
		// System.out.println("accessType :: "+accessType);

		JSONObject strategyObject = extractStrategyInformation(userid);

		for (Object s : strategyObject.keySet()) {			
			System.out.println(s);
		}

		if (("Yes").equalsIgnoreCase(allInfo)) {
			strategyClassInfo = sendAllInfo(strategyName, strategyType,
					strategyObject);
		} else {
			strategyClassInfo = sendJustClassInfo(strategyName, strategyType,
					strategyObject);
		}

		System.out.println("Strategy info we got is :: " + strategyClassInfo);

		try {
			/*JsonRespond
					.respond(strategyClassInfo.toJSONString(), response, 200);*/
			JsonRespond.createSingleStrategyResponseJSON(strategyClassInfo.toJSONString(), response, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static JSONObject sendJustClassInfo(String strategyName,
			String strategyType, JSONObject strategyObject) {
		// TODO Auto-generated method stub
		JSONObject tempClassInfo = new JSONObject();
		if (strategyObject != null) {
			List<Object> arrList = new ArrayList<Object>();
			String tempName = "";
			arrList = (List<Object>) strategyObject.get(strategyType);
			for (int i = 0; i < arrList.size(); i++) {
				tempName = (String) (((HashMap) arrList.get(i)).get("name"));
				System.out.println(tempName);
				if (tempName.equalsIgnoreCase(strategyName)) {
					JSONObject classInfo = (JSONObject) (((HashMap) arrList
							.get(i)).get("class"));
					System.out.println("Class Info ::" + classInfo);
					System.out.println(classInfo.getClass().getName());
					tempClassInfo.put("class", classInfo);
					break;
				}
			}
		}
		return tempClassInfo;
	}

	private static JSONObject sendAllInfo(String strategyName, String strategyType,
			JSONObject strategyObject) {
		// TODO Auto-generated method stub
		JSONObject tempStrategyInfo = new JSONObject();
		if (strategyObject != null) {
			List<Object> arrList = new ArrayList<Object>();
			String tempName = "";
			arrList = (List<Object>) strategyObject.get(strategyType);
			for (int i = 0; i < arrList.size(); i++) {
				tempName = (String) (((HashMap) arrList.get(i)).get("name"));
				System.out.println(tempName);
				if (tempName.equalsIgnoreCase(strategyName)) {					
					tempStrategyInfo = (JSONObject) ((HashMap) arrList.get(i));
					break;
				}
			}
		}
		return tempStrategyInfo;
	}

	private static JSONObject extractStrategyInformation(String userid) {
		String sytaregyFile = "";
		JSONObject strategyObject = null;
		JSONParser parser = new JSONParser();

		try {
			strategyObject = Util.getJSONObject(ObjectStoreHandler.getFileStr(
					userid + "_strategy.json", STRATEGY_CONTAINER));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return strategyObject;
	}
	
	public static void main(String[] args) {
		JSONObject strategyObject = extractStrategyInformation("demo_user1@cisco.com");
		System.out.println(strategyObject);
		JSONObject strategyClassInfo = sendJustClassInfo("test", "MP",
				strategyObject);
		System.out.println("Output is :; ");
		System.out.println(strategyClassInfo);
	}

}

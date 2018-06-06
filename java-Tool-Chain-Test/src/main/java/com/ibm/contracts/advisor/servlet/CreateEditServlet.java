package com.ibm.contracts.advisor.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.implementation.StrategyDAO;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.Util;

public class CreateEditServlet extends HttpServlet implements Constants {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	// The following post method will save a strategy in object storage
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletContext sc = this.getServletContext();
		HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		String userid = null;
		String jsonData = null;
		String strategyName = null, accesstype = null, stratType = null, modifyFlag = null;
		JSONObject jsonObject = new JSONObject();
		try {
			// userid = request.getParameter("userId");
			userid = (String) session.getAttribute("userId");
			SOP.printSOPSmall("User Id :: " + userid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			jsonData = request.getParameter("param_1");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			strategyName = request.getParameter("strategyName");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			accesstype = request.getParameter("accessType");
			accesstype = accesstype.substring(0, 3);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			stratType = request.getParameter("stratType");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			modifyFlag = request.getParameter("modifyFlag");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		System.out.println("StrategyName :: " + strategyName);
		System.out.println("accesstype :: " + accesstype);
		System.out.println("stratType :: " + stratType);
		System.out.println("modifyFlag :: " + modifyFlag);

		System.out.println("=========== Inside getParameter ==============");
		System.out.println("Json Data is Type :: "
				+ jsonData.getClass().getName());
		System.out.println("Json Data is :: " + jsonData);

		try {
			if (modifyFlag.equalsIgnoreCase("D")) {
				StrategyDAO.delFromStrategyTable(userid, strategyName,
						stratType);
			} else if (modifyFlag.equalsIgnoreCase("I")) {
				StrategyDAO.insertIntoStrategyTable(userid, strategyName,
						accesstype, stratType);
			} else if (modifyFlag.equalsIgnoreCase("U")) {
				StrategyDAO.delFromStrategyTable(userid, strategyName,
						stratType);
				StrategyDAO.insertIntoStrategyTable(userid, strategyName,
						accesstype, stratType);
			} else {
				System.out.println("No database action is taken...");
			}

		} catch (Exception se) {
			se.printStackTrace();
		}
		
		
		//Deleting from Obj Storage
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
		
		jsonObject.put("status", "Success");
		try {
			JsonRespond.createLoginResponseJSON(jsonObject.toJSONString(),
					response, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	// Getting strategy Information from Object Storage
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
	
	

}

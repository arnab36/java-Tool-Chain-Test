package com.ibm.contracts.advisor.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.implementation.StrategyDAO;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.SOP;

public class StrategyInfoServlet extends HttpServlet implements Constants{ 
	
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
			String stratType = null;
			String userRole = null;
			JSONObject strategyInfo = null;
			JSONObject strategyObject = new JSONObject();
			JSONObject generalHierarchy =  new JSONObject();
			
			System.out.println("============ Inside StrategyInfoServlet =========================");
					
			try {				
				userid = (String)session.getAttribute("userId");
				SOP.printSOPSmall("User Id :: " + userid);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {			
				stratType=request.getParameter("stratType");
			}catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {			
				userRole=request.getParameter("role");
			}catch (Exception e1) {
				e1.printStackTrace();
			}
			
			System.out.println("stratType :: " + stratType);
			System.out.println("user ID :: " + userid);
			
			generalHierarchy = AnalyseServlet.getJSONFromObjectStorage(stratType, "GEN",
					"classHierarchy");
			
			//Getting all the strategy information and responding
			try {
				//strategyInfo = StrategyDAO.selectFromStrategy(userid,stratType,userRole);
				//strategyObject.put("strategy", strategyInfo.toString());
				System.out.println("========== Strategy Info ========= :; "+strategyInfo);
				JsonRespond.createResponseForHomeToViewStrategyJSON(generalHierarchy.toJSONString(),response, 200);
				//JsonRespond.respond(strategyObject.toJSONString(), response, 200);
			}catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}
}

package com.ibm.contracts.advisor.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.Util;

// The following servlet will get Strategy Information from Object Storage for a particular admin

public class GetAdminStrategyInfo extends HttpServlet  implements Constants{ 
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject strategyObject = new JSONObject();
		
		ServletContext sc = this.getServletContext();
		StringBuffer errorMsg = new StringBuffer();
		HttpSession session = SessionHelper.sessionHelp(request, sc, response);
		String userid = null;
		try {
			userid = (String) session.getAttribute("userId");
			SOP.printSOPSmall("User Id :: " + userid);
		} catch (Exception e1) {
			errorMsg.append("Error in getting user id\n" + e1.toString());
			e1.printStackTrace();
		}			

		try {
			strategyObject =Util.getJSONObject(ObjectStoreHandler.getFileStr(userid+"_strategy.json", STRATEGY_CONTAINER)) ;
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		try {
			JsonRespond.createStrategyHierarchyResponseJSON(strategyObject.toJSONString(),response, 200);					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}




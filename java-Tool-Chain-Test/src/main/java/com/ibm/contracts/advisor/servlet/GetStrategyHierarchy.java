package com.ibm.contracts.advisor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.Util;

public class GetStrategyHierarchy extends HttpServlet implements Constants {

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JSONObject classHierarchyCISCO_GEN_MP = new JSONObject();
		JSONObject classHierarchyCISCO_GEN_MS = new JSONObject();
		// JSONObject classHierarchyCISCO_STATIC_MP = new JSONObject();
		// JSONObject classHierarchyCISCO_STATIC_MS = new JSONObject();

		classHierarchyCISCO_GEN_MP = getStrategyHeirarchyFromObjStorage("MP",
				"GEN");
		classHierarchyCISCO_GEN_MS = getStrategyHeirarchyFromObjStorage("MS",
				"GEN");
		// classHierarchyCISCO_STATIC_MP =
		// getStrategyHeirarchyFromObjStorage("MP","STATIC");
		// classHierarchyCISCO_STATIC_MS =
		// getStrategyHeirarchyFromObjStorage("MS","STATIC");

		try {
			JsonRespond.createStrategyHierarchyResponseJSON(
					classHierarchyCISCO_GEN_MP.toJSONString(),
					classHierarchyCISCO_GEN_MS.toJSONString(), response, 200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static JSONObject getStrategyHeirarchyFromObjStorage(
			String strategyType, String processingType) {
		JSONObject strategyObject = null;
		try {
			strategyObject = Util.getJSONObject(ObjectStoreHandler.getFileStr(
					"classHierarchyCISCO_" + processingType + "_"
							+ strategyType + ".json", PROPERTIES_CONTAINER));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strategyObject;
	}
}

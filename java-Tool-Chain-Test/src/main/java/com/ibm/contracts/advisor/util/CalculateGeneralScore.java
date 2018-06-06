package com.ibm.contracts.advisor.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.callable.ConversationCallableWithLessWCCiscoVCAP;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.implementation.DocumentsDAO;
import com.ibm.contracts.advisor.parser.Parser;
import com.ibm.contracts.advisor.vo.ClassificationVO;



public class CalculateGeneralScore implements Constants {
	public static String getConversationAPIResult(String docConversionResult, String jsonObjectName, String userid) {
		String finalResult = null;
		StringBuffer errorMsg = new StringBuffer();
		StringBuffer log = new StringBuffer();

		if (docConversionResult != null) {
			List parsedlist = null;
			try {
				parsedlist = new Parser().parseString(docConversionResult);
			} catch (Exception e) {
				errorMsg.append("Error parsing conversion result ::"
						+ e.toString());
				e.printStackTrace();
			}
			long startTime = System.currentTimeMillis();
			Map<Integer, JSONObject> list = null;
			try {

				list = APICall(parsedlist);
				if (list.get(-1) != null) {
					errorMsg.append("Number of API call failed with percentage above threshold");
				}

			} catch (Exception e1) {
				errorMsg.append("Exception in CLASSIFIER api call.Try after sometime ::  "
						+ e1.toString());
				e1.printStackTrace();
			}
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			log.append("\n ***************************** \n CLASSIFIER call Time :: "
					+ elapsedTime + " \n *********************************");
			SOP.printSOPSmall("***************************** \n CLASSIFIER call Time :: "
					+ elapsedTime + " \n *********************************");
			JSONArray classificationResult = new JSONArray();
			StringBuffer sbfr = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				JSONObject tmp = null;
				try {
					tmp = list.get(i);
				} catch (Exception e) {
					System.err.println(e);
				}
				classificationResult.add(tmp);
			}

			String finalString = sbfr.toString();
			try {
				finalResult = PostClassifyCleaner.PostClassifyCleanerMain(list,
						docConversionResult);
				if (finalResult != null) {

					DocumentsDAO dao = new DocumentsDAO();
					System.out.println("Json Object Name :: " + jsonObjectName);
					System.out.println("User Name :: " + userid);
					dao.updateAfterNLC(jsonObjectName, userid);

				}
			} catch (Exception e) {
				errorMsg.append("Error in post NLC processing or error updating database ::"
						+ e.toString());
				e.printStackTrace();
			}
			long stopTimePost = System.currentTimeMillis();
			long elapsedTimePost = stopTimePost - stopTime;
			log.append("\n ***************************** \n Post CLASSIFIER processing Time :: "
					+ elapsedTimePost + " \n *********************************");
			SOP.printSOPSmall("***************************** \n Post CLASSIFIER processing Time :: "
					+ elapsedTimePost + " \n *********************************");
			log.append("\n Analyze successful !!!!!!!!!!");
		}
		
		return finalResult;
	}

	
	
	// New Version of the above function and will be commented later
	private static Map<Integer, JSONObject> APICall(List parsedlist)
			throws InterruptedException, ExecutionException {
		List<Future<ClassificationVO>> list = null;
		int failCount = 0;
		Map<Integer, JSONObject> resultlist = new HashMap<Integer, JSONObject>();
		// if NLC is true, do NLC. Else if Conversation is true, do Conversation
		if (classification.equalsIgnoreCase("NLC")) {
			// do NLC with thread mechanism
			int threads = THREADSNLC;
			list = new NLCAPIRunner().runNlc(parsedlist, threads);
		} else if (classification.equalsIgnoreCase("CONV")) {
			if (lessWC != workFlowConfigWORDCOUNT) {
				// do Conversation with thread mechanism
				int threads = THREADSCONVERSATION;
				list = new ConversationAPIRunner().runConversation(parsedlist,
						threads);
			} else {
				// do Conversation with thread mechanism
				int threads = THREADSCONVERSATION;
				if(!CISCOVCAP){
					list = new ConversationWithLessWCAPIRunner().runConversation(
							parsedlist, threads);
				}else{
					list = new ConversationWithLessWCCISCOVCAPAPIRunner().runConversation(
							parsedlist, threads);
				}
				
			}
		}
		for (Future<ClassificationVO> future : list) {
			resultlist
					.put(future.get().getThreadNo(), future.get().getResult());
			if (future.get().isExecuted() == false)
				failCount = failCount + 1;
		}
		/*System.out.println("            failed percentage            " + 100.0
				* ((double) failCount / list.size()));*/
		// System.exit(0);
		if (100.0 * ((double) failCount / list.size()) > Constants.APIFailPer) {
			JSONObject x = new JSONObject();
			x.put("error",
					"failed percentage :"
							+ Double.toString(100.0 * ((double) failCount / list
									.size())));
			resultlist.put(-1, x);
		}

		return resultlist;
	}

}
package com.ibm.contracts.advisor.callable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class ConversationCallableWithLessWCCiscoVCAP implements Constants,
		Callable<ClassificationVO> {

	private String sentence = "";
	private ConversationService service;
	private int threadNum;
	private boolean executed = false;
	private boolean APIcallable = false;
	//public static String CLASS_WORKSPACE_ID = "935c3e97-3d4c-4763-9c76-a84322bb7c1e";

	//

	public ConversationCallableWithLessWCCiscoVCAP(String sentence,
			ConversationService service, int threadNum) {
		// TODO Auto-generated constructor stub
		this.sentence = sentence;
		this.service = service;
		this.threadNum = threadNum;
	}

	public synchronized ClassificationVO call() throws Exception {
		return callAsync();
	}

	public ClassificationVO callAsync() throws Exception {
		MessageRequest newMessage = new MessageRequest.Builder()
				.alternateIntents(true).inputText(sentence).build();
		MessageResponse response = null;
		int wordCount = Util.wordCount(patternWords, sentence);
		if (wordCount > 10) {
			for (int i = 0; i < CONVERSATIONRETRIES; i++) {
				try {
					APIcallable = true;
					response = service.message(APICredentials.CLASS_WORKSPACE_ID, newMessage)
							.execute();
					executed = true;
					break;
				} catch (Exception e) {
					System.out
							.println(sentence + " conv error " + e.toString());

					e.getMessage();
				}
			}
		}
		sentence = sentence.replaceAll(PostNLCPropSet.patternComp, "COMPANY");
		sentence = sentence.replaceAll(PostNLCPropSet.patternSup, "SUPPLIER");
		List<String> className = new LinkedList<String>();
		List<String> conf = new LinkedList<String>();
		JSONObject tmp = null;
		try {
			JSONObject convRes = Util.getJSONObject(response.toString());

			// System.out.println("*************got score*********"+score);
			for (int k = 0; k < 3; k++) {
				className
						.add(((JSONObject) ((JSONArray) convRes.get("intents"))
								.get(k)).get("intent").toString());
				// System.out.println(className);
				// if formula is applicable for confidence then
				if (k == 0 && withFormula == true) {
					double score = getScoreViaFormula(convRes);
					conf.add(Double.toString(score));
				}

				// if formula is not applicable
				else
					conf.add(((JSONObject) ((JSONArray) convRes.get("intents"))
							.get(k)).get("confidence").toString());
				// System.out.println(conf);
			}

		} catch (Exception e) {
			System.out.println(e.toString()
					+ " inside processing the conv result");

		}
		tmp = Util.createTemplateOP(sentence, className, conf);
		System.out.println(tmp.toJSONString()
				+ "------------------------------");
		// System.exit(0);
		if (APIcallable == true && executed == false)
			PostNLCPropSet.Failed = PostNLCPropSet.Failed + "\n" + sentence;
		if (APIcallable == false)
			executed = true;
		ClassificationVO cVo = new ClassificationVO(this.threadNum,
				tmp.toString(), executed);
		return cVo;
	}

	/**
	 * Gets the score: calculate the absolute score using formula
	 *
	 * @param convRes
	 *            : the response we got from conversation
	 * @return the score
	 */
	private double getScoreViaFormula(JSONObject convRes) {
		double overallTotal = 0.0;
		for (int j = 0; j < ((JSONArray) convRes.get("intents")).size(); j++) {
			overallTotal = overallTotal
					+ Double.parseDouble(((JSONObject) ((JSONArray) convRes
							.get("intents")).get(j)).get("confidence")
							.toString());
		}
		double subTotal = 0.0;
		for (int h = 0; h < 3; h++) {
			subTotal = subTotal
					+ Double.parseDouble(((JSONObject) ((JSONArray) convRes
							.get("intents")).get(h)).get("confidence")
							.toString());
		}
		double topConf = Double.parseDouble(((JSONObject) ((JSONArray) convRes
				.get("intents")).get(0)).get("confidence").toString());
		double baseScore = topConf / subTotal;
		double additionalScore = (1.0 - baseScore) * baseScore;
		double score = baseScore + additionalScore;
		if (score > 1.0)
			score = 1.0;
		return score;
	}

}

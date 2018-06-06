package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.ConversationCallableWithLessWCCiscoVCAP;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;

public class ConversationWithLessWCCISCOVCAPAPIRunner implements Constants {


	public List runConversation(List textList, int threads) {
	//	CiscoVcapUtils.getConversationCredentials(CiscoVcapUtils.VCAPUtils());
		String CONVERSATION_USERNAME = CiscoVcapUtils.convUsername;
		String CONVERSATION_PASSWORD = CiscoVcapUtils.convPassword;
		String CONVERSATION_URL = CiscoVcapUtils.convUrl;
		
		/*ConversationService service = new ConversationService(
				ConversationService.VERSION_DATE_2017_02_03);*/
		
		ConversationService service = new ConversationService(
				CiscoVcapUtils.ENV_CONVERSATION_VERSION);
		
		service.setUsernameAndPassword(CONVERSATION_USERNAME,
				CONVERSATION_PASSWORD);
		List<Future<ClassificationVO>> resultlist = new ArrayList<Future<ClassificationVO>>();
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < textList.size(); i++) {
			String temp = (String) textList.get(i);
			java.util.concurrent.Callable worker = new ConversationCallableWithLessWCCiscoVCAP(
					temp, service, i);
			Future<ClassificationVO> submit = executor.submit(worker);
			resultlist.add(submit);

		}
		for (Future<ClassificationVO> future : resultlist) {
			executor.shutdown();
		}
		return resultlist;
	}

}

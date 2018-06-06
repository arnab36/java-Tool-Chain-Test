/**
 * 
 */
package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.ConversationCallableWithLessWC;
import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;

/**
 * @author Atrijit
 *
 */
public class ConversationWithLessWCAPIRunner implements Constants{
	

	/**
	 * 
	 */
	
	
	/**
	 * @param textList : list of text need to pass to Conversation API
	 * @param threads : Number of parallel threads 
	 * 
	 * the function will create the number of threads mentioned and execute all the threads
	 * @return return the result list in json format
	 */
	public List runConversation(List textList, int threads){
		
		//ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);		
		ConversationService service = new ConversationService(VcapSetupParser.ENV_CONVERSATION_VERSION);
		
		if(VCAPCONVERSATION){
		//	VcapSetupParser.setConversationCredentials(VcapSetupParser.VCAPUtils());			
			service.setUsernameAndPassword(VcapSetupParser.VCAPCONVERSATION_userID, VcapSetupParser.VCAPCONVERSATION_password);
		}else {
			service.setUsernameAndPassword(APICredentials.CONVERSATION_USERNAME, APICredentials.CONVERSATION_PASSWORD);
		}	
		
		
		List<Future<ClassificationVO>> resultlist = new ArrayList<Future<ClassificationVO>>();
		ExecutorService executor = Executors.newFixedThreadPool(threads);
				for (int i = 0; i < textList.size(); i++) {
				String temp = (String)textList.get(i);
				java.util.concurrent.Callable worker = new ConversationCallableWithLessWC(temp,service,i);
				Future<ClassificationVO> submit = executor.submit(worker);
				resultlist.add(submit);        
			}
		
		
		for (Future<ClassificationVO> future : resultlist) {
			executor.shutdown();
			}
		
		return resultlist;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConversationAPIRunner conversationAPIRunner = new ConversationAPIRunner();
	}

}

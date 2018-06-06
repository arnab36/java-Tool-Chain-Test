/**
 * 
 */
package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.ConversationCallable;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.contracts.advisor.constants.APICredentials;

/**
 * @author Atrijit
 *
 */
public class ConversationAPIRunner implements Constants{
	

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
		
		ConversationService service = null;
		if(VCAPCONVERSATION){
			service = new ConversationService(VcapSetupParser.ENV_CONVERSATION_VERSION);
		}else{
			service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
		}		
		
		service.setUsernameAndPassword(APICredentials.CONVERSATION_USERNAME, APICredentials.CONVERSATION_PASSWORD);
		List<Future<ClassificationVO>> resultlist = new ArrayList<Future<ClassificationVO>>();
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		
		
			for (int i = 0; i < textList.size(); i++) {
				String temp = (String)textList.get(i);
				java.util.concurrent.Callable worker = new ConversationCallable(temp,service,i);
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

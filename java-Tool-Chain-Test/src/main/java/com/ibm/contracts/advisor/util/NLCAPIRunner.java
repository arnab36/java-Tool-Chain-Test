package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.NLCCallable;
import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
public class NLCAPIRunner implements Constants{

	public NLCAPIRunner() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Future<ClassificationVO>> runNlc(List textList, int threads) throws InterruptedException, ExecutionException{
		NaturalLanguageClassifier service = new NaturalLanguageClassifier();
		service.setUsernameAndPassword(APICredentials.nlcusername,APICredentials.nlcpassword);
		List<Future<ClassificationVO>> tmpResultlist = new ArrayList<Future<ClassificationVO>>();
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		//System.out.println("***********************************number of NLC call *******************"+textList.size());
		for (int i = 0; i < textList.size(); i++) {
			String temp = (String)textList.get(i);
			java.util.concurrent.Callable worker = new NLCCallable(temp,service,i);
			Future<ClassificationVO> submit = executor.submit(worker);
			
			//System.out.println("---got string-------"+submit.get());
			/*try{
				resultlist.put(submit.get().getThreadNo(), submit.get().getResult());
			}catch(Exception e){
				resultlist.put(submit.get().getThreadNo(), "");
			}*/
			tmpResultlist.add(submit);
          //  System.out.println(i+" ------------ "+textList.size());
        
		}
		for ( Future<ClassificationVO> future : tmpResultlist) {
           /* try {
                   System.out.println(future.get());
            } catch (Exception e) {
                    e.printStackTrace();
            } 
*/
            executor.shutdown();
    } 
		System.out.println("-----returning resultset-----------");
		return tmpResultlist;
	}
	
	
	
	
	

}

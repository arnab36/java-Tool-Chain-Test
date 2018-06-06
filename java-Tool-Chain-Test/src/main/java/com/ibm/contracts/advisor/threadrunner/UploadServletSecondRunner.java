package com.ibm.contracts.advisor.threadrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.UploadServletSecondCallable;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.UploadServletSecondOPVO;

public class UploadServletSecondRunner implements Constants {

	public static  List<UploadServletSecondOPVO> run(String templateFileName, String staticTempleateName ) {
		// TODO Auto-generated constructor stub
		List<UploadServletSecondOPVO> returnList = new ArrayList<UploadServletSecondOPVO>();
		
		ExecutorService executor = Executors
				.newFixedThreadPool(Constants.uploadServletSecondThreadCount);
		
				
		/*List<String> list = new ArrayList<String>();
		list.add(0,templateFileName);
		list.add(1,staticTempleateName);		*/
		
		Map<String,String> map = new  HashMap<String,String>();
		map.put(UploadServletSecondThreadTemplateFileName, templateFileName);
		map.put(UploadServletSecondThreadStaticTemplateName, staticTempleateName);		
		
		for(String key: map.keySet()){
			Callable<UploadServletSecondOPVO> worker = new UploadServletSecondCallable(key,map.get(key));
			Future<UploadServletSecondOPVO> future = executor.submit(worker);
			
			while (!future.isDone()) {
				System.out.println("Task is not completed yet....");
				try {
					Thread.sleep(UploadServletSecondThreadSleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Task is completed, let's check result");

				try {
					UploadServletSecondOPVO outputVO = future.get();
					returnList.add(outputVO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		/*for (int i = 0; i < list.size(); i++) { 
			String inputString = (String) list.get(i);			
			Callable<UploadServletSecondOPVO> worker = new UploadServletSecondCallable(inputString);
			Future<UploadServletSecondOPVO> future = executor.submit(worker);
			
			while (!future.isDone()) {
				System.out.println("Task is not completed yet....");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Task is completed, let's check result");

				try {
					UploadServletSecondOPVO outputVO = future.get();
					returnList.add(outputVO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}*/
		
		return returnList;		
	}

}

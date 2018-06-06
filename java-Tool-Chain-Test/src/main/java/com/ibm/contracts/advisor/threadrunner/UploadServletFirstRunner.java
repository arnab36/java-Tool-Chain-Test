package com.ibm.contracts.advisor.threadrunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.UploadServletFirstCallable;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.ConvertReturn;
import com.ibm.contracts.advisor.vo.UploadServletFirstOPVO;
import com.ibm.contracts.advisor.vo.UploadServletFirstVO;

public class UploadServletFirstRunner implements Constants{
	
	public static List<UploadServletFirstOPVO> run(File file){
		
		List<UploadServletFirstOPVO> returnList = new ArrayList<UploadServletFirstOPVO>();
		
		ExecutorService executor = Executors
				.newFixedThreadPool(Constants.uploadServletFirstThreadCount);
		
				
		UploadServletFirstVO uvo1 = new UploadServletFirstVO();
		uvo1.setFile(file);
		uvo1.setType(UploadServletAnswers);		
		
		UploadServletFirstVO uvo2 = new UploadServletFirstVO();
		uvo2.setFile(file);
		uvo2.setType(UploadServletConvertRes);
		
		List<UploadServletFirstVO> list = new ArrayList<UploadServletFirstVO>();
		list.add(uvo1);
		list.add(uvo2);
		
		for (int i = 0; i < list.size(); i++) { 
			UploadServletFirstVO inputVo = (UploadServletFirstVO) list.get(i);			
			Callable<UploadServletFirstOPVO> worker = new UploadServletFirstCallable(inputVo);
			Future<UploadServletFirstOPVO> future = executor.submit(worker);
			
			while (!future.isDone()) {
				System.out.println("Task is not completed yet....");
				try {
					Thread.sleep(UploadServletFirstThreadSleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Task is completed, let's check result");

				try {
					UploadServletFirstOPVO outputVO = future.get();
					returnList.add(outputVO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		return returnList;
	}
}

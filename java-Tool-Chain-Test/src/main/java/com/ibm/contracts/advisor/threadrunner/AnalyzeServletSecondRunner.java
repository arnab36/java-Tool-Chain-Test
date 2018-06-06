package com.ibm.contracts.advisor.threadrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ibm.contracts.advisor.callable.AnalyzeServletSecondCallable;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.vo.AnalyzeServletSecondOPVO;
import com.ibm.contracts.advisor.vo.AnalyzeServletSecondVO;

public class AnalyzeServletSecondRunner implements Constants {

	public static List<AnalyzeServletSecondOPVO> run(String contractType) {

		List<AnalyzeServletSecondOPVO> returnList = new ArrayList<AnalyzeServletSecondOPVO>();

		ExecutorService executor = Executors
				.newFixedThreadPool(Constants.analyseServletSecondThreadCount);

		AnalyzeServletSecondVO uvo1 = new AnalyzeServletSecondVO();
		uvo1.setContractType(contractType);
		uvo1.setClassType("GEN");
		uvo1.setCallType("classHierarchy");
		
		AnalyzeServletSecondVO uvo2 = new AnalyzeServletSecondVO();
		uvo2.setContractType(contractType);
		uvo2.setClassType("GEN");
		uvo2.setCallType("HelpClasses");
		
		AnalyzeServletSecondVO uvo3 = new AnalyzeServletSecondVO();
		uvo3.setContractType(contractType);
		uvo3.setClassType("STATIC");
		uvo3.setCallType("classHierarchy");
		
		AnalyzeServletSecondVO uvo4 = new AnalyzeServletSecondVO();
		uvo4.setContractType(contractType);
		uvo4.setClassType("STATIC");
		uvo4.setCallType("HelpClasses");
		
		AnalyzeServletSecondVO uvo5 = new AnalyzeServletSecondVO();
		uvo5.setContractType(contractType);
		uvo5.setClassType("DYNAMIC");
		uvo5.setCallType("classHierarchy");
		
		AnalyzeServletSecondVO uvo6 = new AnalyzeServletSecondVO();
		uvo6.setContractType(contractType);
		uvo6.setClassType("DYNAMIC");
		uvo6.setCallType("HelpClasses");


		List<AnalyzeServletSecondVO> list = new ArrayList<AnalyzeServletSecondVO>();
		list.add(uvo1);
		list.add(uvo2);
		list.add(uvo3);
		list.add(uvo4);
		list.add(uvo5);
		list.add(uvo6);

		for (int i = 0; i < list.size(); i++) {

			AnalyzeServletSecondVO inputVo = (AnalyzeServletSecondVO) list
					.get(i);

			Callable<AnalyzeServletSecondOPVO> worker = new AnalyzeServletSecondCallable(
					inputVo.getContractType(), inputVo.getClassType(),
					inputVo.getCallType());

			Future<AnalyzeServletSecondOPVO> future = executor.submit(worker);

			while (!future.isDone()) {
				System.out.println("Task is not completed yet....");
				try {
					Thread.sleep(analyseServletSecondThreadThreadSleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Task is completed, let's check result");

				try {
					AnalyzeServletSecondOPVO outputVO = future.get();
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

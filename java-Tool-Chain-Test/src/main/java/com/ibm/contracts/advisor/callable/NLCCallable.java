package com.ibm.contracts.advisor.callable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.util.Util;
import com.ibm.contracts.advisor.vo.ClassificationVO;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifiedClass;

public class NLCCallable implements Constants, Callable<ClassificationVO>{

	private  String sentence="";
	private NaturalLanguageClassifier service;
	private int threadNum;
	private boolean executed=false;
	private boolean APIcallable=false;
	
	public NLCCallable(String sentence,NaturalLanguageClassifier service,int threadNum) {
		// TODO Auto-generated constructor stub
		
		this.sentence = sentence;
		this.service = service;
		this.threadNum=threadNum;
	}
	
	public synchronized ClassificationVO call() throws Exception {
		return callAsync();
	}
	
	
	public ClassificationVO callAsync() throws Exception {
		boolean bool = false;
		
		Classification classification = null;
		Classification classificationFinal = new Classification();
		List<ClassifiedClass> topClass=new LinkedList<ClassifiedClass>();
		for(int i = 0; i <NLCRETRIES; i++) {
		try {
			//System.out.println("sentence lengh is -----"+sentence.length());
			if(sentence.length()>1024){
				
				sentence=sentence.substring(0, 1023);
			}
			sentence=sentence.replaceAll(PostNLCPropSet.patternComp, "COMPANY");
			sentence=sentence.replaceAll(PostNLCPropSet.patternSup, "SUPPLIER");	
			int wordCount=Util.wordCount(patternWords, sentence);
			if(wordCount<SplitPropSetWORDCOUNT){
				if(wordCount>5){
					//System.out.println("==calling NLC for word "+wordCount);
					APIcallable=true;
					classification = service.classify(APICredentials.NLC_LANG_Classifier_ID,
							sentence).execute();
					//System.out.println(classification.toString());
					List<ClassifiedClass> classes = classification.getClasses();
					for(int j=0;j<3;j++)
						topClass.add(classes.get(j));
					classificationFinal.setClasses(topClass);
					classificationFinal.setText(classification.getText());
					
				}
				
								
			}
			executed=true;
			break;
		} catch (Exception e) {
			System.out.println("------comming inside exception");
			e.getMessage();
	    }
		}
		if(APIcallable ==false)
			executed=true;
		ClassificationVO nlcVo=new ClassificationVO(this.threadNum,classificationFinal.toString(),executed);
		return nlcVo;
	}
	

}

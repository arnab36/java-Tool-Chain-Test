package com.ibm.contracts.advisor.util;

import org.apache.log4j.Logger;


public class SOP {		
	final static Logger logger = Logger.getLogger(SOP.class);
	
	public static void printSOP(String args){
		if(logger.isDebugEnabled()){
		    logger.debug("This is debug: "+args);
		}
		logger.error("This is error : " + args);
		
	}
	
	public static void printSOPSmall(String args){
	/*	if(logger.isDebugEnabled()){
		    logger.debug("This is debug: "+args);
		}
		logger.error("This is error : " + args);*/
		System.out.println(args);
	}
}
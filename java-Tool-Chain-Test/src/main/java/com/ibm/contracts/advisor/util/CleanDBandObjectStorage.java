package com.ibm.contracts.advisor.util;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;


public class CleanDBandObjectStorage implements Constants{
	
	/**
	 * TO DO. Implementation depends on requirement
	 * */
	// Clearing the dashDB entry
	public void cleanDashDB(String objectName){
		
	}
	
	
	// Clearing the object storage
	public static void cleanObjectStorage(String jsonObjectName, String htmlObjectName){
		
		try {
			ObjectStoreHandler.delete(PROCESSING_CONTAINER, "STATIC_"+jsonObjectName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			ObjectStoreHandler.delete(PROCESSING_CONTAINER, "GENERAL_"+jsonObjectName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			ObjectStoreHandler.delete(PROCESSING_CONTAINER, htmlObjectName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
package com.ibm.contracts.advisor.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.constants.PostNLCPropSet;
import com.ibm.contracts.advisor.db.DBPool;
import com.ibm.contracts.advisor.handler.ObjectStoreHandler;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;

public class InitServlet extends HttpServlet implements Constants {

	public void init() throws ServletException {

		String customConfig = null, stdLanguage = null;
		
		try {
			Connection conn = DBPool.getConnection();
			System.out.println("dbpool conn: " + conn);
			DBPool.closeConnection(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			PostNLCPropSet.intiMoneyClass();
		}catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// If set, the CISCO VCAP will set the environment variable values only once at the beginning 
		if(CISCOVCAP){
			CiscoVcapUtils.getDocumentConversionCredentials(CiscoVcapUtils.VCAPUtils());
			CiscoVcapUtils.getConversationCredentials(CiscoVcapUtils.VCAPUtils());
			CiscoVcapUtils.setEnvironmentVariable();	
		}else {
			// The following functions will set up the vcap values only once
			VcapSetupParser.setEnvironmentVariable();	
			
			if(VCAPCONVERSATION){ 				
				VcapSetupParser.setConversationCredentials(VcapSetupParser.VCAPUtils());
						
			}
			
			if(VCAPDOCUMENTCONVERSION){
				VcapSetupParser.getDocumentConversionCredentials(VcapSetupParser.VCAPUtils());
			}
		}
		
		
		
		
		//System.out.println("===================== The custom config is :: ==============="+ customConfig);

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

}

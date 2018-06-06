package com.ibm.contracts.advisor.driver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.contracts.advisor.util.StaticScoreCalculator;
import com.ibm.contracts.advisor.util.Util;

public class CalculateFGStatic {

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException, ExecutionException,
			ParseException {
			
		
		JSONParser jsonParser = new JSONParser();
		double score = 0;
		JSONObject docJsonObject_STATIC = null,templateJsonObject;
		String  fileName = "";
		
		// Loading static template
		String classTemplate = "C:/Users/IBM_ADMIN/Documents/Phase2/CISCO-Phase 3/MP_FG_Static_Template_V5.json";
		JSONObject standardTemplate = null;
		try {
			standardTemplate = Util.getJSONFromFile(classTemplate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String docConversionStaticResult = "C:/Users/IBM_ADMIN/Documents/Phase2/CISCO-Phase 3/tempStatic.json";
		try {
			docJsonObject_STATIC = Util.getJSONFromFile(docConversionStaticResult);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
		//System.out.println("FG standardTemplate :: "+ standardTemplate);
		
		score = StaticScoreCalculatorDriver.templateMatchFunction(
				docJsonObject_STATIC, standardTemplate, fileName);

		System.out.println("docJsonObject_STATIC :: \n " + docJsonObject_STATIC);
		System.out.println("Score is :: " +score);
	}
}
